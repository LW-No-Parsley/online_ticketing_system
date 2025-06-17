<template>
  <!-- 订单管理主容器 -->
  <div>
    <!-- 订单数据表格 -->
    <el-table 
      :data="orders"          
      stripe                 
      border                  
      highlight-current-row   
      style="width: 99.9%"    
      :header-cell-style="{   
        background: '#f8f9fa', 
        color: '#495057'
      }"
    >
      <!-- 订单ID列 -->
      <el-table-column 
        prop="orderId" 
        label="订单ID" 
        width="100"
        align="center"
      />
      <!-- 日期列 -->
      <el-table-column 
        prop="date" 
        label="日期" 
        width="120"
        align="center"
      />
      <!-- 时间列 -->
      <el-table-column 
        prop="time" 
        label="时间" 
        width="100"
        align="center"
      />
      <!-- 状态列 -->
      <el-table-column 
        prop="status" 
        label="状态" 
        width="120"
        align="center"
      >
        <template #default="scope">
          <!-- 状态标签，不同状态显示不同颜色 -->
          <el-tag 
            :type="scope.row.status === '已完成' ? 'success' : 
                  (scope.row.status === '待支付' ? 'warning' : 'info')"
          >
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 用户名列 -->
      <el-table-column 
        prop="username" 
        label="用户名" 
        width="120"
        align="center"
      />
      <!-- 总金额列 -->
      <el-table-column 
        prop="totalAmount" 
        label="总金额(元)" 
        width="120"
        align="right"
      >
        <template #default="scope">
          <!-- 格式化金额显示两位小数 -->
          {{ scope.row.totalAmount.toFixed(2) }}
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column 
        label="操作"
        width="180"
        align="center"
      >
        <template #default="scope">
          <!-- 详情按钮 -->
          <el-button 
            size="small"
            @click="showOrderDetail(scope.row)"
          >
            详情
          </el-button>
          <!-- 更新状态按钮 -->
          <el-button 
            type="primary" 
            size="small"
            @click="showUpdateDialog(scope.row)"
          >
            更新状态
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 更新订单状态对话框 -->
    <el-dialog
      v-model="updateDialogVisible"
      title="更新订单状态"
      width="30%"
    >
      <!-- 状态选择下拉框 -->
      <el-select 
        v-model="selectedStatus" 
        placeholder="请选择状态"
        style="width: 100%"
      >
        <el-option
          v-for="item in statusOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <!-- 对话框底部按钮 -->
      <template #footer>
        <el-button @click="updateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateOrderStatus">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
// 导入HTTP请求库
import axios from 'axios';
// 导入Element Plus消息提示组件
import { ElMessage } from 'element-plus';

export default {
  name: 'OrderManagement',
  data() {
    return {
      orders: [], // 存储订单列表数据
      updateDialogVisible: false, // 控制更新对话框显示
      currentOrder: null, // 当前操作的订单
      statusOptions: [ // 订单状态选项
        { label: '待支付', value: '待支付' },
        { label: '已支付', value: '已支付' },
        { label: '已完成', value: '已完成' },
        { label: '已取消', value: '已取消' }
      ],
      selectedStatus: '' // 选中的状态
    };
  },
  // 生命周期钩子 - 组件创建时调用
  created() {
    this.fetchOrders(); // 获取订单数据
  },
  methods: {
    /**
     * 显示更新订单状态对话框
     * @param {Object} order - 订单对象
     */
    showUpdateDialog(order) {
      this.currentOrder = order;
      this.selectedStatus = order.status;
      this.updateDialogVisible = true;
    },

    /**
     * 更新订单状态
     */
    updateOrderStatus() {
      const token = localStorage.getItem('token');
      if (!token) {
        ElMessage.error('请先登录');
        this.$router.push('/admin-login');
        return;
      }

      // 发送更新订单状态请求
      axios
        .post('http://localhost:8080/api/orders/update-status', {
          orderId: this.currentOrder.orderId,
          status: this.selectedStatus
        }, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        .then((response) => {
          if (response.data.success) {
            ElMessage.success(response.data.message);
            this.fetchOrders(); // 刷新订单列表
          } else {
            ElMessage.error(response.data.message || '更新状态失败');
          }
          this.updateDialogVisible = false;
        })
        .catch((error) => {
          ElMessage.error(error.response?.data?.message || '更新状态失败');
          console.error('更新订单状态错误:', error);
          // 如果token过期或无效，跳转到登录页
          if (error.response && error.response.status === 401) {
            this.$router.push('/admin-login');
          }
          this.updateDialogVisible = false;
        });
    },

    /**
     * 显示订单详情
     * @param {Object} order - 订单对象
     */
    showOrderDetail(order) {
      // 生成票种信息表格行
      const ticketRows = order.tickets?.map(ticket => `
        <tr>
          <td>${ticket.name}</td>
          <td>${ticket.price.toFixed(2)}元</td>
          <td>${ticket.count}</td>
          <td>${(ticket.price * ticket.count).toFixed(2)}元</td>
        </tr>
      `).join('') || '';

      // 显示订单详情弹窗
      this.$alert(`
        <div>
          <h3>订单详情</h3>
          <p><strong>订单ID：</strong>${order.orderId}</p>
          <p><strong>日期：</strong>${order.date}</p>
          <p><strong>时间：</strong>${order.time}</p>
          <p><strong>状态：</strong>${order.status}</p>
          <p><strong>用户名：</strong>${order.username}</p>
          <p><strong>总金额：</strong>${order.totalAmount.toFixed(2)}元</p>
          
          <h4>票种信息</h4>
          <table border="1" cellpadding="5" style="width:100%;border-collapse:collapse;">
            <thead>
              <tr>
                <th>票种名称</th>
                <th>单价</th>
                <th>数量</th>
                <th>小计</th>
              </tr>
            </thead>
            <tbody>
              ${ticketRows}
            </tbody>
          </table>
        </div>
      `, '订单详情', {
        dangerouslyUseHTMLString: true, // 允许HTML内容
        customClass: 'order-detail-dialog' // 自定义样式类
      });
    },
    
    /**
     * 获取订单列表数据
     */
    fetchOrders() {
      const token = localStorage.getItem('token');
      if (!token) {
        ElMessage.error('请先登录');
        this.$router.push('/admin-login');
        return;
      }

      // 发送获取订单列表请求
      axios
        .get('http://localhost:8080/api/admin/orders', {
          headers: {
            'Accept': 'application/json',
            'Authorization': `Bearer ${token}`
          }
        })
        .then((response) => {
          if (Array.isArray(response.data)) {
            this.orders = response.data; // 更新订单列表
          } else {
            ElMessage.error('获取订单数据失败: 数据格式不正确');
          }
        })
        .catch((error) => {
          ElMessage.error('获取订单数据失败');
          console.error('获取订单数据错误:', error);
          // 如果token过期或无效，跳转到登录页
          if (error.response && error.response.status === 401) {
            this.$router.push('/admin-login');
          }
        });
    },
  },
};
</script>

<style scoped>
/* 订单详情对话框样式 */
.order-detail-dialog {
  min-width: 600px; /* 最小宽度 */
}

/* 订单详情表格样式 */
.order-detail-dialog table {
  margin-top: 10px; /* 上边距 */
}

/* 订单详情表头样式 */
.order-detail-dialog th {
  background-color: #f5f7fa; /* 背景色 */
  text-align: center; /* 文字居中 */
}

/* 订单详情单元格样式 */
.order-detail-dialog td {
  padding: 8px; /* 内边距 */
  text-align: center; /* 文字居中 */
}

/* 主表格样式 */
.el-table {
  margin-top: 20px; /* 上边距 */
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 阴影效果 */
}

/* 移除表格底部边框 */
.el-table::before {
  height: 0;
}

/* 标签样式 */
.el-tag {
  margin: 2px; /* 外边距 */
}

/* 选择列单元格样式 */
.el-table-column--selection .cell {
  padding-left: 10px;
  padding-right: 10px;
}

/* 表头单元格样式 */
.el-table th.el-table__cell {
  background-color: #f5f7fa; /* 背景色 */
}

/* 带边框表格的单元格样式 */
.el-table--border .el-table__cell {
  border-right: 1px solid #ebeef5; /* 右边框 */
}

/* 移除表格底部边框 */
.el-table--border::after, 
.el-table--group::after {
  width: 0;
}
</style>
