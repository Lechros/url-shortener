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
		Spinner,
		Table,
		TableBody,
		TableBodyCell,
		TableBodyRow,
		TableHead,
		TableHeadCell,
		Toggle
	} from 'flowbite-svelte';
	import {
		ArrowLeftOutline,
		ArrowRightOutline,
		ArrowUpRightFromSquareOutline,
		QuestionCircleSolid
	} from 'flowbite-svelte-icons';
	import { create, type CreateResponse } from '$lib/api/create';
	import { getList, type ListResponse } from '$lib/api/list';
	import { disableUrl, enableUrl } from '$lib/api/enable';
	import { BACKEND_URL } from '$lib/shared';

	let now = $state(new Date());

	$effect(() => {
		const intervalId = setInterval(() => {
			now = new Date();
		}, 500);
		return () => clearInterval(intervalId);
	});

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

	let createState = $state<'idle' | 'pending' | 'error' | 'success'>('idle');
	let result = $state<CreateResponse>();
	let message = $state('');

	const handleSubmit = async () => {
		if (createState === 'pending') return;

		if (!url) {
			message = 'URL을 입력해주세요.';
			return;
		}

		createState = 'pending';

		const expiresAt = expire > 0 ? new Date(Date.now() + expire * 1000).toISOString() : undefined;
		await create({ url, alias, expiresAt })
			.then((data) => {
				createState = 'success';
				result = data;
				message = '';
				lastUpdateTime = Date.now();
			})
			.catch((error) => {
				createState = 'error';
				result = undefined;
				message = error;
			});
	};

	let lastUpdateTime = $state(Date.now());
	let urlList = $state<ListResponse['content']>([]);
	let page = $state(0);
	let totalPages = $state(0);

	$effect(() => {
		getList(page).then((data) => {
			urlList = data.content;
			page = data.page;
			totalPages = data.totalPages;
		});
		// For reactive after url create
		lastUpdateTime;
	});
</script>

<main class="container mx-auto flex flex-col items-start p-4">
	<Heading tag="h1" customSize="text-4xl font-semibold mt-8">URL 단축기</Heading>
	<P class="mt-4">URL 단축기에 오신 것을 환영합니다.</P>

	<div class="mt-8 grid grid-cols-[384px_1fr] gap-8">
		<div>
			<Card>
				<form class="flex flex-col gap-4">
					<div>
						<Label for="url" class="mb-1">URL</Label>
						<Input
							type="text"
							id="url"
							bind:value={url}
							placeholder="URL을 입력해주세요"
							required
						/>
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
						<Select
							id="expire"
							class="mt-1"
							items={expireItems}
							bind:value={expire}
							placeholder=""
						/>
					</Label>
					<Button type="submit" disabled={createState === 'pending'} onclick={handleSubmit}>
						{#if createState === 'pending'}
							<Spinner class="me-3" size="4" color="white" />
						{/if}
						단축 URL 생성하기
					</Button>
				</form>
				{#if result}
					<!-- TODO: 로직 lib으로 분리 -->
					{@const shortUrl = `${BACKEND_URL}/${result.alias}`}
					<Hr hrClass="mt-6" />
					<div class="mt-4 flex flex-col">
						<P>
							URL이 생성되었습니다.
							<A href={shortUrl} target="_blank">{shortUrl}</A>
						</P>
						{#if result.expiresAt}
							<P>만료 시간: {result.expiresAt.toLocaleString()}</P>
						{/if}
					</div>
				{/if}
				{#if message}
					<P class="mt-4 text-red-500">{message}</P>
				{/if}
			</Card>
		</div>

		<div>
			<div class="flex justify-between">
				<Heading tag="h2" class="text-2xl font-semibold">URL 목록</Heading>
				<div class="flex items-center gap-4">
					<Button outline class="p-2" disabled={page === 0} onclick={() => page--}>
						<ArrowLeftOutline class="size-4" />
					</Button>
					<div class="whitespace-nowrap">{page + 1} / {totalPages}</div>
					<Button outline class="p-2" disabled={page === totalPages - 1} onclick={() => page++}>
						<ArrowRightOutline class="size-4" />
					</Button>
				</div>
			</div>
			<Table hoverable class="mt-4 table-fixed">
				<TableHead>
					<TableHeadCell class="w-24">활성화</TableHeadCell>
					<TableHeadCell class="w-40">Alias</TableHeadCell>
					<TableHeadCell>URL</TableHeadCell>
					<TableHeadCell class="w-48">만료 시간</TableHeadCell>
					<TableHeadCell class="w-16"></TableHeadCell>
				</TableHead>
				<TableBody>
					{#each urlList as item (item.id)}
						<TableBodyRow>
							<TableBodyCell>
								<Toggle
									checked={!item.disabled}
									onchange={(e) => {
										const target = e.currentTarget;
										if (target.checked) {
											enableUrl(item.id).catch(() => {
												alert('활성화에 실패했습니다.');
												target.checked = false;
											});
										} else {
											disableUrl(item.id).catch(() => {
												alert('비활성화에 실패했습니다.');
												target.checked = true;
											});
										}
									}}
								/>
							</TableBodyCell>
							<TableBodyCell>{item.alias}</TableBodyCell>
							<TableBodyCell class="truncate">
								<A href={item.url} target="_blank">{item.url}</A>
							</TableBodyCell>
							{#if item.expiresAt}
								<TableBodyCell class={[item.expiresAt < now && 'text-red-500 line-through']}>
									{item.expiresAt.toLocaleString()}
								</TableBodyCell>
							{:else}
								<TableBodyCell>-</TableBodyCell>
							{/if}
							<TableBodyCell>
								<Button href="{BACKEND_URL}/{item.alias}" target="_blank" class="!p-2">
									<ArrowUpRightFromSquareOutline class="size-4" />
								</Button>
							</TableBodyCell>
						</TableBodyRow>
					{/each}
				</TableBody>
			</Table>
		</div>
	</div>
</main>

<Popover
	triggeredBy="#alias-help"
	class="bg-white text-sm text-gray-900 dark:border-gray-600 dark:bg-gray-800 dark:text-white"
	placement="right"
>
	<div>단축 URL 경로입니다.</div>
</Popover>
