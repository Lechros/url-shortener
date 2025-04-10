package com.lechros.urlshortener.ui.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.lechros.urlshortener.PageInfo
import com.lechros.urlshortener.application.UrlService
import com.lechros.urlshortener.createShortenedUrlCreateRequest
import com.lechros.urlshortener.createShortenedUrlResponse
import com.lechros.urlshortener.support.hashid.encoder.HashIdEncoderImpl
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.ContentResultMatchersDsl
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter

@WebMvcTest(UrlRestController::class)
@Import(UrlRestControllerTestConfig::class, HashIdEncoderImpl::class)
class UrlRestControllerTest(
    private val urlService: UrlService,
    private val webApplicationContext: WebApplicationContext,
    private val objectMapper: ObjectMapper,
) : StringSpec({
    lateinit var mockMvc: MockMvc

    beforeTest {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .build()
    }

    fun MockHttpServletRequestDsl.jsonContent(value: Any) {
        content = objectMapper.writeValueAsString(value)
        contentType = APPLICATION_JSON
    }

    fun ContentResultMatchersDsl.success(value: Any) {
        json(objectMapper.writeValueAsString(ApiResponse.success(value)), JsonCompareMode.STRICT)
    }

    "지정한 alias로 단축 URL을 생성한다" {
        val request = createShortenedUrlCreateRequest()
        val response = createShortenedUrlResponse()
        every { urlService.shortenUrl(any()) } returns response

        // when
        mockMvc.post("/api/create") {
            jsonContent(request)
        }.andExpect {
            status { isOk() }
            content { success(response) }
        }
    }

    "URL이 공백인 요청은 실패한다" {
        val request = createShortenedUrlCreateRequest(url = "")
        val response = createShortenedUrlResponse()
        every { urlService.shortenUrl(any()) } returns response

        mockMvc.post("/api/create") {
            jsonContent(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    "목록 조회가 성공한다" {
        val response = PageInfo(listOf(createShortenedUrlResponse()), 0, 1, 1, 1)
        every { urlService.getShortenedUrlPage(any()) } returns response

        mockMvc.get("/api/list") {}.andExpect {
            status { isOk() }
            content { success(response) }
        }
    }

    "page, size가 포함된 목록 조회가 성공한다" {
        val response = PageInfo(listOf(createShortenedUrlResponse()), 0, 10, 1, 1)
        every { urlService.getShortenedUrlPage(any()) } returns response

        mockMvc.get("/api/list") {
            param("page", "0")
            param("size", "10")
        }.andExpect {
            status { isOk() }
            content { success(response) }
        }
    }

    "size가 20을 넘는 요청은 실패한다" {
        mockMvc.get("/api/list") {
            param("page", "0")
            param("size", "21")
        }.andExpect {
            status { isBadRequest() }
        }
    }
})

@TestConfiguration
class UrlRestControllerTestConfig {
    @Bean
    fun urlService() = mockk<UrlService>()
}
