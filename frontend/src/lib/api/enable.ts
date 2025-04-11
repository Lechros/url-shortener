import { BACKEND_URL } from '$lib/shared';

export function enableUrl(id: string) {
	return fetch(`${BACKEND_URL}/api/${id}/enable`, {
		method: 'POST'
	}).then((res) => {
		if (!res.ok) {
			throw new Error(res.statusText);
		}
	});
}

export function disableUrl(id: string) {
	return fetch(`${BACKEND_URL}/api/${id}/disable`, {
		method: 'POST'
	}).then((res) => {
		if (!res.ok) {
			throw new Error(res.statusText);
		}
	});
}
