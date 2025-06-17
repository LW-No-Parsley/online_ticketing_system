<template>
  <div class="management-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>景区管理</span>
          <div>
            <el-input
              v-model="searchQuery"
              placeholder="搜索景区名称"
              style="width: 200px; margin-right: 10px;"
              clearable
            />
            <el-button type="primary" size="small">新增景区</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="filteredScenicList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="景区名称" />
        <el-table-column prop="location" label="所在地" />
        <el-table-column prop="level" label="景区等级" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === '开放' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button size="small">编辑</el-button>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        class="pagination"
        :page-size="10"
        :total="100"
        layout="prev, pager, next"
      />
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ScenicManagement',
  data() {
    return {
      searchQuery: '',
      scenicList: [
        { id: 1, name: '黄山风景区', location: '安徽黄山', level: '5A', status: '开放' },
        { id: 2, name: '九寨沟景区', location: '四川阿坝', level: '5A', status: '开放' },
        { id: 3, name: '西湖景区', location: '浙江杭州', level: '5A', status: '维护中' },
      ]
    }
  },
  computed: {
    filteredScenicList() {
      return this.scenicList.filter(item => 
        item.name.includes(this.searchQuery)
      )
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
