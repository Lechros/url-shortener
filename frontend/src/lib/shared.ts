import { PUBLIC_BACKEND_URL } from '$env/static/public';

export const BACKEND_URL = PUBLIC_BACKEND_URL;

export type ApiResponse<T> =
	| {
			status: 'success';
			data: T;
	  }
	| {
			status: 'error';
			message: string;
	  };

export type PageInfo<T> = {
	content: T[];
	page: number;
	size: number;
	totalPages: number;
	totalElements: number;
};

export function getDataOrThrow<T>(apiResponse: ApiResponse<T>) {
	switch (apiResponse.status) {
		case 'success':
			return apiResponse.data;
		case 'error':
			throw apiResponse.message;
		default:
			throw '알 수 없는 오류가 발생했습니다.';
	}
}

export function parseDate(isoString: string): Date {
	return new Date(isoString + 'Z');
}
