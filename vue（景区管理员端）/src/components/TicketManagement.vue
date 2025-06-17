<template>
  <div class="management-container">
    <el-tabs type="border-card">
      <el-tab-pane label="票种管理">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>票种管理</span>
              <el-button type="primary" size="small" @click="showAddDialog">新增票种</el-button>
            </div>
          </template>
      
          <el-table 
            :data="ticketList.slice((ticketPage.currentPage - 1) * ticketPage.pageSize, ticketPage.currentPage * ticketPage.pageSize)" 
            border 
            style="width: 100%"
          >
            <el-table-column prop="name" label="票种名称" />
            <el-table-column prop="price" label="价格" />
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button size="small" @click="showEditDialog(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="ticketPage.currentPage"
            v-model:page-size="ticketPage.pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="ticketList.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleTicketSizeChange"
            @current-change="handleTicketCurrentChange"
          />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="票库存管理">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>票库存管理</span>
              <el-button type="primary" size="small" @click="showAddInventoryDialog">新增票库存</el-button>
            </div>
          </template>

          <el-table 
            :data="groupedInventoryList.slice((inventoryPage.currentPage - 1) * inventoryPage.pageSize, inventoryPage.currentPage * inventoryPage.pageSize)" 
            border 
            style="width: 100%"
          >
            <el-table-column prop="date" label="日期" />
            <el-table-column label="上午票数">
              <template #default="scope">
                {{ scope.row.morning ? scope.row.morning.totalTickets : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="上午已售">
              <template #default="scope">
                {{ scope.row.morning ? scope.row.morning.soldTickets : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="下午票数">
              <template #default="scope">
                {{ scope.row.afternoon ? scope.row.afternoon.totalTickets : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="下午已售">
              <template #default="scope">
                {{ scope.row.afternoon ? scope.row.afternoon.soldTickets : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="总票数">
              <template #default="scope">
                {{ (scope.row.morning?.totalTickets || 0) + (scope.row.afternoon?.totalTickets || 0) }}
              </template>
            </el-table-column>
            <el-table-column label="总已售">
              <template #default="scope">
                {{ (scope.row.morning?.soldTickets || 0) + (scope.row.afternoon?.soldTickets || 0) }}
              </template>
            </el-table-column>
            <el-table-column label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220">
              <template #default="scope">
                <el-button size="small" @click="showEditInventoryDialog(scope.row.morning || scope.row.afternoon)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDeleteInventory(scope.row.morning || scope.row.afternoon)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="inventoryPage.currentPage"
            v-model:page-size="inventoryPage.pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="inventoryList.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleInventorySizeChange"
            @current-change="handleInventoryCurrentChange"
          />
        </el-card>

        <!-- 新增票库存对话框 -->
        <el-dialog v-model="addInventoryDialogVisible" title="新增票库存" width="30%">
          <el-form :model="addInventoryForm" label-width="100px">
            <el-form-item label="日期">
              <el-date-picker v-model="addInventoryForm.date" type="date" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="时段">
              <el-select v-model="addInventoryForm.time" placeholder="请选择时段">
                <el-option label="上午" value="morning" />
                <el-option label="下午" value="afternoon" />
              </el-select>
            </el-form-item>
            <el-form-item label="总票数">
              <el-input-number v-model="addInventoryForm.totalTickets" :min="1" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="addInventoryDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleAddInventory">确定</el-button>
          </template>
        </el-dialog>

        <!-- 编辑票库存对话框 -->
        <el-dialog v-model="editInventoryDialogVisible" title="编辑票库存" width="30%">
          <el-form :model="editInventoryForm" label-width="100px">
            <el-form-item label="日期">
              <el-date-picker v-model="editInventoryForm.date" type="date" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="时段">
              <el-select v-model="editInventoryForm.time" placeholder="请选择时段">
                <el-option label="上午" value="morning" />
                <el-option label="下午" value="afternoon" />
              </el-select>
            </el-form-item>
            <el-form-item label="总票数">
              <el-input-number v-model="editInventoryForm.totalTickets" :min="1" />
            </el-form-item>
            <el-form-item label="状态">
              <el-switch
                v-model="editInventoryForm.status"
                :active-value="1"
                :inactive-value="0"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="editInventoryDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleEditInventory">确定</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增票种对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增票种" width="30%">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="票种名称">
          <el-input v-model="addForm.name" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="addForm.price" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑票种对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑票种" width="30%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="票种名称">
          <el-input v-model="editForm.newName" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="editForm.price" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080'
})

// 请求拦截器 - 添加token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

export default {
  name: 'TicketManagement',
  data() {
    return {
      ticketList: [],
      inventoryList: [],
      groupedInventoryList: [],
      ticketPage: {
        currentPage: 1,
        pageSize: 10
      },
      inventoryPage: {
        currentPage: 1,
        pageSize: 10
      },
      addDialogVisible: false,
      editDialogVisible: false,
      addInventoryDialogVisible: false,
      editInventoryDialogVisible: false,
      addForm: {
        name: '',
        price: 0
      },
      editForm: {
        oldName: '',
        newName: '',
        price: 0
      },
      addInventoryForm: {
        date: '',
        time: 'morning',
        totalTickets: 100
      },
      editInventoryForm: {
        id: null,
        date: '',
        time: 'morning',
        totalTickets: 100,
        status: 1
      }
    }
  },
  created() {
    this.fetchTicketTypes()
    this.fetchInventory()
  },
  methods: {
    handleTicketSizeChange(val) {
      this.ticketPage.pageSize = val
    },
    handleTicketCurrentChange(val) {
      this.ticketPage.currentPage = val
    },
    handleInventorySizeChange(val) {
      this.inventoryPage.pageSize = val
    },
    handleInventoryCurrentChange(val) {
      this.inventoryPage.currentPage = val
    },
    // 票库存相关方法
    async fetchInventory() {
      try {
        const response = await api.get('/api/ticket-inventory')
        this.inventoryList = response.data.sort((a, b) => {
          return new Date(b.date) - new Date(a.date) // 按日期降序排列
        })
        
        // 按日期分组
        const grouped = {}
        this.inventoryList.forEach(item => {
          if (!grouped[item.date]) {
            grouped[item.date] = {
              date: item.date,
              status: item.status,
              morning: null,
              afternoon: null
            }
          }
          // 确保正确匹配时段
          if (item.time === 'morning' || item.time === '上午') {
            grouped[item.date].morning = item
          } else if (item.time === 'afternoon' || item.time === '下午') {
            grouped[item.date].afternoon = item
          }
        })
        this.groupedInventoryList = Object.values(grouped)
      } catch (error) {
        this.$message.error('获取票库存列表失败')
      }
    },
    showAddInventoryDialog() {
      this.addInventoryForm = {
        date: '',
        time: 'morning',
        totalTickets: 100
      }
      this.addInventoryDialogVisible = true
    },
    async handleAddInventory() {
      try {
        const response = await api.post('/api/ticket-inventory', {
          date: this.addInventoryForm.date,
          time: this.addInventoryForm.time,
          totalTickets: this.addInventoryForm.totalTickets,
          soldTickets: 0,
          status: 1,
          dayStatus: 0
        })
        this.$message.success('票库存添加成功')
        this.addInventoryDialogVisible = false
        this.fetchInventory()
      } catch (error) {
        this.$message.error('添加票库存失败')
      }
    },
    showEditInventoryDialog(inventory) {
      this.editInventoryForm = {
        id: inventory.id,
        date: inventory.date,
        time: inventory.time,
        totalTickets: inventory.totalTickets,
        status: inventory.status
      }
      this.editInventoryDialogVisible = true
    },
    async handleEditInventory() {
      try {
        await api.put(`/api/ticket-inventory/${this.editInventoryForm.id}`, {
          date: this.editInventoryForm.date,
          time: this.editInventoryForm.time,
          totalTickets: this.editInventoryForm.totalTickets,
          status: this.editInventoryForm.status
        })
        this.$message.success('票库存更新成功')
        this.editInventoryDialogVisible = false
        this.fetchInventory()
      } catch (error) {
        this.$message.error('更新票库存失败')
      }
    },
    async handleDeleteInventory(inventory) {
      try {
        await this.$confirm('确定删除该票库存记录吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await api.delete(`/api/ticket-inventory/${inventory.id}`)
        this.$message.success('票库存删除成功')
        this.fetchInventory()
      } catch (error) {
        if (error === 'cancel') return
        this.$message.error('删除票库存失败')
      }
    },
    async fetchTicketTypes() {
      try {
        const response = await api.get('/api/ticket-types')
        this.ticketList = response.data
      } catch (error) {
        this.$message.error('获取票种列表失败')
      }
    },
    showAddDialog() {
      this.addForm = { name: '', price: 0 }
      this.addDialogVisible = true
    },
    async handleAdd() {
      try {
        await api.post('/api/ticket-types', null, {
          params: {
            name: this.addForm.name,
            price: this.addForm.price
          }
        })
        this.$message.success('票种添加成功')
        this.addDialogVisible = false
        this.fetchTicketTypes()
      } catch (error) {
        if (error.response && error.response.data === '票种已存在') {
          this.$message.error('票种已存在')
        } else {
          this.$message.error('添加票种失败')
        }
      }
    },
    showEditDialog(ticket) {
      this.editForm = {
        oldName: ticket.name,
        newName: ticket.name,
        price: ticket.price
      }
      this.editDialogVisible = true
    },
    async handleEdit() {
      try {
        await api.put('/api/ticket-types', null, {
          params: {
            oldName: this.editForm.oldName,
            newName: this.editForm.newName,
            price: this.editForm.price
          }
        })
        this.$message.success('票种修改成功')
        this.editDialogVisible = false
        this.fetchTicketTypes()
      } catch (error) {
        if (error.response && error.response.data === '票种不存在') {
          this.$message.error('票种不存在')
        } else if (error.response && error.response.data === '新票种名称已存在') {
          this.$message.error('新票种名称已存在')
        } else {
          this.$message.error('修改票种失败')
        }
      }
    },
    async handleDelete(ticket) {
      try {
        await this.$confirm('确定删除该票种吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await api.delete('/api/ticket-types', {
          params: {
            name: ticket.name
          }
        })
        this.$message.success('票种删除成功')
        this.fetchTicketTypes()
      } catch (error) {
        if (error === 'cancel') {
          return
        }
        if (error.response && error.response.data === '票种不存在') {
          this.$message.error('票种不存在')
        } else {
          this.$message.error('删除票种失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.management-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>
