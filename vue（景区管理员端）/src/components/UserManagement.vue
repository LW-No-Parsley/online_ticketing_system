<template>
  <!-- 用户管理主容器 -->
  <div>
    <!-- 用户数据表格 -->
    <el-table 
      :data="users"          
      stripe                  
      border                  
      highlight-current-row   
      style="width: 99.9%"    
      :header-cell-style="{   
        background: '#f8f9fa', 
        color: '#495057'
      }"
    >
      <!-- ID列 -->
      <el-table-column 
        prop="id" 
        label="ID" 
        width="80"
        align="center"
      />
      <!-- 用户名列 -->
      <el-table-column 
        prop="username" 
        label="用户名(手机号)" 
        width="150"
        align="center"
      />
      <!-- 邮箱列 -->
      <el-table-column 
        prop="email" 
        label="邮箱" 
        min-width="200"
      />
      <!-- 注册时间列 -->
      <el-table-column 
        prop="time"
        label="注册时间"
        width="180"
        align="center"
      />
      <!-- 操作列 -->
      <el-table-column 
        label="操作"
        width="120"
        align="center"
      >
        <template #default="scope">
          <!-- 删除按钮 -->
          <el-button 
            type="danger" 
            size="small" 
            @click="deleteUser(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
// 导入HTTP请求库
import axios from 'axios';
// 导入Element Plus消息提示组件
import { ElMessage } from 'element-plus';

export default {
  name: 'UserManagement',
  data() {
    return {
      users: [], // 存储用户列表数据
    };
  },
  // 生命周期钩子 - 组件创建时调用
  created() {
    this.fetchUsers(); // 获取用户数据
  },
  methods: {
    /**
     * 获取用户列表数据
     */
    fetchUsers() {
      // 从本地存储获取token
      const token = localStorage.getItem('token');
      if (!token) {
        ElMessage.error('请先登录');
        this.$router.push('/admin-login');
        return;
      }

      // 发送获取用户列表请求
      axios
        .get('http://localhost:8080/api/admin/users', {
          headers: {
            'Accept': 'application/json',
            'Authorization': `Bearer ${token}`
          }
        })
        .then((response) => {
          if (Array.isArray(response.data)) {
            this.users = response.data; // 更新用户列表
          } else {
            ElMessage.error('获取用户数据失败: 数据格式不正确');
          }
        })
        .catch((error) => {
          ElMessage.error('获取用户数据失败');
          console.error('获取用户数据错误:', error);
          // 如果token过期或无效，跳转到登录页
          if (error.response && error.response.status === 401) {
            this.$router.push('/admin-login');
          }
        });
    },

    /**
     * 删除用户
     * @param {number} id - 用户ID
     */
    deleteUser(id) {
      // 从本地存储获取token
      const token = localStorage.getItem('token');
      if (!token) {
        ElMessage.error('请先登录');
        this.$router.push('/admin-login');
        return;
      }

      // 确认对话框
      this.$confirm('确定要删除该用户吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 发送删除用户请求
        axios
          .delete(`http://localhost:8080/api/admin/users/${id}`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          })
          .then((response) => {
            if (response.data.success) {
              ElMessage.success(response.data.message);
              this.fetchUsers(); // 刷新用户列表
            } else {
              ElMessage.error(response.data.message || '删除用户失败');
            }
          })
          .catch((error) => {
            ElMessage.error(error.response?.data?.message || '删除用户失败');
            console.error('删除用户错误:', error);
            // 如果token过期或无效，跳转到登录页
            if (error.response && error.response.status === 401) {
              this.$router.push('/admin-login');
            }
          });
      }).catch(() => {
        // 用户取消删除
        ElMessage.info('已取消删除');
      });
    },
  },
};
</script>

<style scoped>
/* 表格样式 */
.el-table {
  margin-top: 20px; /* 上边距 */
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 阴影效果 */
}

/* 移除表格底部边框 */
.el-table::before {
  height: 0;
}

/* 表头样式 */
.el-table th.el-table__cell {
  background-color: #f5f7fa; /* 表头背景色 */
}

/* 表格边框样式 */
.el-table--border .el-table__cell {
  border-right: 1px solid #ebeef5; /* 单元格右边框 */
}

/* 行悬停效果 */
.el-table__body tr:hover > td {
  background-color: #f1f3f5 !important; /* 悬停背景色 */
}

/* 单元格内边距 */
.el-table .cell {
  padding: 12px 16px; /* 单元格内边距 */
}

/* 按钮样式 */
.el-table .el-button {
  padding: 6px 10px; /* 按钮内边距 */
  border-radius: 3px; /* 圆角 */
}

/* 选择列样式 */
.el-table-column--selection .cell {
  padding-left: 10px;
  padding-right: 10px;
}

/* 移除表格底部边框 */
.el-table--border::after, 
.el-table--group::after {
  width: 0;
}
</style>
