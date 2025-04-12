import { type ApiResponse, BACKEND_URL, getDataOrThrow, parseDate } from '$lib/shared';

export type CreateRequest = {
	url: string;
	alias: string;
	expiresAt?: string;
};

type CreateResponseRaw = {
	url: string;
	alias: string;
	createdAt: string;
	expiresAt?: string;
	disabled: boolean;
	deleted: boolean;
	id: string;
};

export type CreateResponse = {
	url: string;
	alias: string;
	createdAt: Date;
	expiresAt?: Date;
	disabled: boolean;
	deleted: boolean;
	id: string;
};

export function create(request: CreateRequest): Promise<CreateResponse> {
	return fetch(`${BACKEND_URL}/api/create`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(request)
	})
		.then<ApiResponse<CreateResponseRaw>>((res) => res.json())
		.then<CreateResponse>((json) => {
			const data = getDataOrThrow(json);
			return {
				...data,
				createdAt: parseDate(data.createdAt),
				expiresAt: data.expiresAt ? parseDate(data.expiresAt) : undefined
			};
		});
}
