<script lang="ts">
	import {
		A,
		Button,
		Card,
		Heading,
		Hr,
		Input,
		Label,
		P,
		Popover,
		Select,
		Spinner
	} from 'flowbite-svelte';
	import { QuestionCircleSolid } from 'flowbite-svelte-icons';
	import { PUBLIC_BACKEND_URL } from '$env/static/public';

	interface UrlCreateSuccessResponse {
		url: string;
		alias: string;
		createdAt: string;
		expiresAt?: string;
		disabled: boolean;
		deleted: boolean;
		id: string;
	}

	let url = $state('');
	let alias = $state('');
	let expire = $state(-1);
	const expireItems = [
		{ value: -1, name: '무제한' },
		{ value: 10, name: '10초' },
		{ value: 60, name: '1분' },
		{ value: 10 * 60, name: '10분' },
		{ value: 60 * 60, name: '1시간' },
		{ value: 24 * 60 * 60, name: '1일' },
		{ value: 7 * 24 * 60 * 60, name: '일주일' },
		{ value: 30 * 24 * 60 * 60, name: '1개월' },
		{ value: 365 * 24 * 60 * 60, name: '1년' }
	];

	let fetchState = $state<'idle' | 'pending' | 'error' | 'success'>('idle');
	let result = $state<UrlCreateSuccessResponse>();
	let message = $state('');

	const handleSubmit = async () => {
		if (fetchState === 'pending') return;

		if (!url) {
			message = 'URL을 입력해주세요.';
			return;
		}

		fetchState = 'pending';

		const expiresAt = expire > 0 ? new Date(Date.now() + expire * 1000).toISOString() : undefined;
		try {
			// TODO: API lib으로 분리
			const res = await fetch(PUBLIC_BACKEND_URL + '/api/create', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({
					url,
					alias,
					expiresAt
				})
			});
			const json = await res.json();
			const status = json.status;
			console.log(json);
			if (status === 'success') {
				fetchState = 'success';
				result = json.data;
				message = '';
			} else {
				fetchState = 'error';
				result = undefined;
				message = json.message ?? '알 수 없는 오류가 발생했습니다.';
			}
		} catch (e) {
			fetchState = 'error';
			result = undefined;
			message = '서버와의 연결에 실패했습니다. ' + e;
		}
	};
</script>

<main class="container mx-auto flex flex-col items-start p-4">
	<Heading tag="h1" customSize="text-4xl font-semibold mt-8">URL 단축기</Heading>
	<P class="mt-4">URL 단축기에 오신 것을 환영합니다.</P>

	<Card class="mt-8">
		<form class="flex flex-col gap-4">
			<div>
				<Label for="url" class="mb-1">URL</Label>
				<Input type="text" id="url" bind:value={url} placeholder="URL을 입력해주세요" required />
			</div>
			<div>
				<Label for="alias" class="mb-1 flex items-center">
					Alias
					<button id="alias-help">
						<QuestionCircleSolid class="ms-1 size-4" />
					</button>
				</Label>
				<Input
					type="text"
					id="alias"
					bind:value={alias}
					placeholder="입력하지 않으면 임의로 생성됩니다"
				/>
			</div>
			<Label>
				링크 만료 시간
				<Select id="expire" class="mt-1" items={expireItems} bind:value={expire} placeholder="" />
			</Label>
			<Button type="submit" disabled={fetchState === 'pending'} onclick={handleSubmit}>
				{#if fetchState === 'pending'}
					<Spinner class="me-3" size="4" color="white" />
				{/if}
				단축 URL 생성하기
			</Button>
		</form>
		{#if result}
			<!-- TODO: 로직 lib으로 분리 -->
			{@const shortUrl = `${PUBLIC_BACKEND_URL}/${result.alias}`}
			<Hr hrClass="mt-6" />
			<div class="mt-4 flex flex-col">
				<P>
					URL이 생성되었습니다.
					<A href={shortUrl} target="_blank">{shortUrl}</A>
				</P>
				{#if result.expiresAt}
					<P>만료 시간: {new Date(result.expiresAt + 'Z').toLocaleString()}</P>
				{/if}
			</div>
		{/if}
		{#if message}
			<P class="mt-4 text-red-500">{message}</P>
		{/if}
	</Card>
</main>

<Popover
	triggeredBy="#alias-help"
	class="bg-white text-sm text-gray-900 dark:border-gray-600 dark:bg-gray-800 dark:text-white"
	placement="right"
>
	<div>단축 URL 경로입니다.</div>
</Popover>
