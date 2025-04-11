import {
	type ApiResponse,
	BACKEND_URL,
	getDataOrThrow,
	type PageInfo,
	parseDate
} from '$lib/shared';

type ListResponseRaw = PageInfo<{
	url: string;
	alias: string;
	createdAt: string;
	expiresAt?: string;
	disabled: boolean;
	deleted: boolean;
	id: string;
}>;

export type ListResponse = PageInfo<{
	url: string;
	alias: string;
	createdAt: Date;
	expiresAt?: Date;
	disabled: boolean;
	deleted: boolean;
	id: string;
}>;

export function getList(page: number = 0): Promise<ListResponse> {
	return fetch(`${BACKEND_URL}/api/list?page=${page}&size=10`, {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
		}
	})
		.then<ApiResponse<ListResponseRaw>>((res) => res.json())
		.then<ListResponse>((json) => {
			const data = getDataOrThrow(json);
			return {
				...data,
				content: data.content.map((item) => ({
					...item,
					createdAt: parseDate(item.createdAt),
					expiresAt: item.expiresAt ? parseDate(item.expiresAt) : undefined
				}))
			};
		});
}
