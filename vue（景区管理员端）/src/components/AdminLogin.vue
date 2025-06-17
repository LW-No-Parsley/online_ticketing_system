<template>
  <!-- 登录页面容器 -->
  <div class="login-container">
    <!-- 登录表单 -->
    <el-form 
      :model="loginForm" 
      :rules="rules" 
      ref="loginFormRef" 
      label-width="100px"
    >
      <h2 class="login-title">管理员登录</h2>
      
      <!-- 用户名输入框 -->
      <el-form-item label="用户名" prop="username">
        <el-input 
          v-model="loginForm.username" 
          placeholder="请输入用户名" 
        />
      </el-form-item>
      
      <!-- 密码输入框 -->
      <el-form-item label="密码" prop="password">
        <el-input 
          v-model="loginForm.password" 
          type="password" 
          placeholder="请输入密码" 
          show-password
        />
      </el-form-item>
      
      <!-- 登录按钮 -->
      <el-form-item>
        <el-button 
          type="primary" 
          @click="handleLogin" 
          class="login-button"
        >
          登录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
// 导入Vue组合式API
import { reactive, ref } from 'vue';
// 导入Element Plus消息提示组件
import { ElMessage } from 'element-plus';
// 导入HTTP请求库
import axios from 'axios';
// 导入路由相关功能
import { useRouter } from 'vue-router';
// 导入加密库
import CryptoJS from 'crypto-js';

export default {
  name: 'AdminLogin',
  setup() {
    // 登录表单数据
    const loginForm = reactive({
      username: '',
      password: '',
    });

    // 表单验证规则
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
      ],
    };

    // 表单引用
    const loginFormRef = ref(null);
    // 路由实例
    const router = useRouter();

    /**
     * 处理登录逻辑
     */
    const handleLogin = () => {
      // 验证表单
      loginFormRef.value.validate((valid) => {
        if (valid) {
          // 密码加密处理(加盐哈希)
          const salt = 'scenic-ticket-salt'; // 加盐值，前后端保持一致
          const hashedPassword = CryptoJS.SHA256(loginForm.password + salt).toString();
          
          // 构造登录请求数据
          const loginData = {
            username: loginForm.username,
            password: hashedPassword
          };
          
          // 发送登录请求
          axios
            .post('http://localhost:8080/api/scenic-admin/login', loginData)
            .then((response) => {
              if (response.data.success) {
                // 调试日志
                console.log('登录响应数据:', response.data);
                // 存储token和用户名到localStorage
                const username = response.data.username || loginForm.username;
                localStorage.setItem('token', response.data.token || 'authenticated');
                localStorage.setItem('username', username);
                // 显示成功消息
                ElMessage.success('登录成功');
                // 跳转到管理员主页面
                router.push('/admin-dashboard'); 
              } else {
                // 显示错误消息
                ElMessage.error(response.data.message || '登录失败');
              }
            })
            .catch((error) => {
              // 显示错误消息
              ElMessage.error('登录失败，请检查用户名或密码');
              console.error('登录错误:', error);
            });
        } else {
          // 表单验证失败提示
          ElMessage.warning('请填写完整的登录信息');
        }
      });
    };

    // 暴露给模板使用的数据和方法
    return {
      loginForm,
      rules,
      loginFormRef,
      handleLogin,
    };
  },
};
</script>

<style scoped>
/* 登录容器样式 - 居中布局 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh; /* 全屏高度 */
  width: 100%;
  background-color: var(--el-bg-color-page); /* 使用Element Plus变量 */
}

/* 登录表单样式 */
.el-form {
  width: 100%;
  max-width: 500px; /* 最大宽度限制 */
  padding: 40px;
  border-radius: 8px;
  background-color: var(--el-bg-color); /* 使用Element Plus变量 */
  box-shadow: var(--el-box-shadow-light); /* 使用Element Plus变量 */
  margin: 0 auto; /* 水平居中 */
}

/* 输入框样式 */
.el-input {
  width: 100%;
}

/* 表单项内容区域样式 */
.el-form-item__content {
  display: block;
  width: 100%;
}

/* 登录标题样式 */
.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: var(--el-text-color-primary); /* 使用Element Plus变量 */
  font-size: 24px;
  font-weight: 500;
}

/* 表单项间距 */
.el-form-item {
  margin-bottom: 22px;
}

/* 登录按钮样式 */
.login-button {
  width: 100%;
  margin-top: 10px;
}
</style>
