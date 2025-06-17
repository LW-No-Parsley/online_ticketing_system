<template>
  <!-- 管理员主布局容器 -->
  <el-container class="admin-container">
    <!-- 顶部导航栏 -->
    <el-header class="admin-header">
      <div class="header-left">景区票务管理系统</div>
      <div class="header-right">
        <!-- 用户信息下拉菜单 -->
        <el-dropdown>
          <span class="el-dropdown-link">
            <!-- 用户头像 -->
            <el-avatar :size="36" :src="userAvatar" />
            <!-- 用户名 -->
            <span class="username">{{ userName }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <!-- 主体内容区域 -->
    <el-container>
      <!-- 左侧导航菜单 -->
      <el-aside width="220px" class="admin-sidebar">
        <el-menu
          :default-active="activeMenu"  
          @select="handleMenuSelect"   
          background-color="#304156"    
          text-color="#bfcbd9"          
          active-text-color="#409EFF"   
        >
          <!-- 用户管理菜单项 -->
          <el-menu-item index="users" :class="{'is-active': $route.path.startsWith('/admin-dashboard/users')}">
            <el-icon><user /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <!-- 订单管理菜单项 -->
          <el-menu-item index="orders" :class="{'is-active': $route.path.startsWith('/admin-dashboard/orders')}">
            <el-icon><document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <!-- 轮播图管理菜单项 -->
          <el-menu-item index="carousels" :class="{'is-active': $route.path.startsWith('/admin-dashboard/carousels')}">
            <el-icon><picture-filled /></el-icon>
            <span>轮播图管理</span>
          </el-menu-item>
          <!-- 票务管理菜单项 -->
          <el-menu-item index="tickets" :class="{'is-active': $route.path.startsWith('/admin-dashboard/tickets')}">
            <el-icon><ticket /></el-icon>
            <span>票务管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <!-- 主内容区域 -->
      <el-main class="admin-main">
        <!-- 路由视图，显示子组件内容 -->
        <router-view v-slot="{ Component }">
          <!-- 路由切换过渡效果 -->
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
// 导入Element Plus图标组件
import {
  User,
  Document,
  Ticket,
  Picture,
  DataAnalysis,
  PictureFilled
} from '@element-plus/icons-vue'

export default {
  name: 'AdminDashboard',
  // 注册子组件
  components: {
    User,
    Document,
    Ticket,
    Picture,
    DataAnalysis,
    PictureFilled
  },
  data() {
    // 从本地存储获取用户名，默认为"管理员"
    const username = localStorage.getItem('username') || '管理员';
    return {
      activeMenu: this.$route.path.split('/').pop() || 'users', // 当前激活的菜单项
      userName: username, // 用户名
      userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' // 用户头像URL
    };
  },
  methods: {
    /**
     * 处理菜单选择事件
     * @param {string} index - 选中的菜单项索引
     */
    handleMenuSelect(index) {
      const path = index.startsWith('/admin-dashboard/') 
        ? index 
        : `/admin-dashboard/${index}`;
      this.$router.push(path); // 路由跳转
    },
    /**
     * 处理退出登录
     */
    handleLogout() {
      localStorage.removeItem('token'); // 清除token
      this.$router.push('/'); // 跳转到首页
      window.location.reload(); // 刷新页面
    }
  },
};
</script>

<style scoped>
/* 主容器样式 */
.admin-container {
  height: 100vh; /* 全屏高度 */
}

/* 顶部导航栏样式 */
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #304156; /* 深蓝色背景 */
  color: #fff; /* 白色文字 */
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08); /* 底部阴影 */
}

/* 左侧标题样式 */
.header-left {
  font-size: 20px;
  font-weight: 600;
}

/* 右侧用户区域样式 */
.header-right {
  display: flex;
  align-items: center;
}

/* 下拉菜单链接样式 */
.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
}

/* 用户名样式 */
.username {
  margin-left: 10px;
}

/* 左侧菜单栏样式 */
.admin-sidebar {
  background-color: #304156; /* 深蓝色背景 */
  height: calc(100vh - 60px); /* 全屏高度减去顶部导航栏高度 */
}

/* 主内容区域样式 */
.admin-main {
  padding: 20px;
  background-color: #f0f2f5; /* 浅灰色背景 */
  min-height: calc(100vh - 60px); /* 最小高度 */
  overflow-y: auto; /* 垂直滚动 */
  height: calc(100vh - 60px); /* 高度 */
}

/* 响应式设计 - 移动端适配 */
@media (max-width: 768px) {
  .el-aside {
    width: 64px !important; /* 缩小侧边栏宽度 */
  }
  
  .el-menu-item span {
    display: none; /* 隐藏菜单文字 */
  }
}
</style>
