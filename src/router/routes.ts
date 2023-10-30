import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') },
      { path: 'sheetparser', component: () => import('pages/SheetParserForm.vue') },
      { path: 'login', component: () => import('components/common/Login.vue'), props: { urlRedirect: "/" } },
      { path: 'logout', component: () => import('components/common/Logout.vue') },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
