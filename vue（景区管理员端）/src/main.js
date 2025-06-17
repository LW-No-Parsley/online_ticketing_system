// 导入全局样式
import './assets/main.css'

// 导入Vue核心功能
import { createApp } from 'vue'
// 导入根组件
import App from './App.vue'
// 导入Vue Router相关功能
import { createRouter, createWebHistory } from 'vue-router';
// 导入各页面组件
import AdminLogin from './components/AdminLogin.vue';
import AdminDashboard from './components/AdminDashboard.vue';
import OrderManagement from './components/OrderManagement.vue';
import TicketManagement from './components/TicketManagement.vue';
import UserManagement from './components/UserManagement.vue';
import CarouselManagement from './components/CarouselManagement.vue';
// 导入Element Plus UI库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

/**
 * 定义应用路由配置
 * @type {Array}
 */
const routes = [
  { 
    path: '/', 
    component: AdminLogin, // 登录页
    meta: { title: '登录' }
  },
  { 
    path: '/admin-dashboard', 
    component: AdminDashboard, // 后台主框架
    meta: { title: '后台管理' },
    children: [
      { path: 'users', component: UserManagement, meta: { title: '用户管理' } },
      { path: 'orders', component: OrderManagement, meta: { title: '订单管理' } },
      { path: 'tickets', component: TicketManagement, meta: { title: '票务管理' } },
      { path: 'carousels', component: CarouselManagement, meta: { title: '轮播图管理' } }
    ]
  }
];

/**
 * 创建路由实例
 */
const router = createRouter({
  history: createWebHistory(), // 使用HTML5历史模式
  routes: routes, // 路由配置
});

/**
 * 全局路由守卫
 * 用于权限控制和路由拦截
 */
router.beforeEach((to, _, next) => {
  // 白名单路径 - 不需要登录即可访问的路径
  const whiteList = ['/'];
  
  // 检查是否已登录 - 从localStorage获取token
  const token = localStorage.getItem('token');
  const isAuthenticated = token && token !== 'authenticated';

  // 已登录用户访问登录页，自动跳转到后台首页
  if (isAuthenticated && (to.path === '/' || to.path === '/login')) {
    return next('/admin-dashboard');
  }

  // 检查当前路径是否在白名单中
  const isWhiteListed = whiteList.includes(to.path);

  if (isWhiteListed) {
    // 白名单路径直接放行
    next();
  } else if (!isAuthenticated) {
    // 未登录且不在白名单，重定向到登录页
    next('/');
  } else {
    // 已登录用户访问非白名单路径，正常放行
    next();
  }
});

// 创建Vue应用实例
const app = createApp(App);

// 使用路由插件
app.use(router);

// 使用Element Plus UI库
app.use(ElementPlus);

// 挂载应用到DOM
app.mount('#app');
