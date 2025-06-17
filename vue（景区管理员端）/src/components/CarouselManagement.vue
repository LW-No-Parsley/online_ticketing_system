<template>
  <div>
    <el-button type="primary" @click="showAddDialog">添加轮播图</el-button>
    
    <el-table :data="carousels" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column label="图片" width="180" align="center">
        <template #default="scope">
          <el-image 
            :src="scope.row.imageUrl" 
            fit="cover" 
            style="width: 150px; height: 80px"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="150" />
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.active ? 'success' : 'danger'">
            {{ scope.row.active ? '激活' : '未激活' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="displayOrder" label="排序" width="80" align="center" />
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button size="small" @click="showEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteCarousel(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="40%"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="轮播图标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :show-file-list="false"
          >
            <el-button type="primary">选择图片</el-button>
            <span v-if="form.file" style="margin-left: 10px">{{ form.file.name }}</span>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.isActive" />
        </el-form-item>
        <el-form-item label="显示顺序">
          <el-input-number v-model="form.displayOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  name: 'CarouselManagement',
  data() {
    return {
      carousels: [],
      dialogVisible: false,
      dialogTitle: '添加轮播图',
      isEditMode: false,
      currentId: null,
      form: {
        title: '',
        file: null,
        isActive: true,
        displayOrder: 0
      }
    };
  },
  created() {
    this.fetchCarousels();
  },
  methods: {
    fetchCarousels() {
      const token = localStorage.getItem('token');
      if (!token || token === 'undefined' || token === 'null') {
        ElMessage.error('请先登录');
        localStorage.removeItem('token');
        this.$router.push('/admin-login');
        return;
      }

      axios.get('http://localhost:8080/api/admin/carousel', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      .then(response => {
        this.carousels = response.data.map(item => ({
          ...item,
          imageUrl: `http://localhost:8080${item.imageUrl}`
        }));
        // 计算最大displayOrder值
        this.maxOrder = this.carousels.reduce((max, item) => 
          Math.max(max, item.displayOrder || 0), 0);
      })
      .catch(error => {
        ElMessage.error('获取轮播图失败');
        console.error(error);
          if (error.response?.status === 401) {
            localStorage.removeItem('token');
            this.$router.push('/admin-login');
          }
      });
    },
    showAddDialog() {
      this.dialogTitle = '添加轮播图';
      this.isEditMode = false;
      this.resetForm();
      this.form.displayOrder = (this.maxOrder || 0) + 1;
      this.dialogVisible = true;
    },
    showEditDialog(carousel) {
      this.dialogTitle = '编辑轮播图';
      this.isEditMode = true;
      this.currentId = carousel.id;
      this.form = {
        title: carousel.title,
        file: null,
        isActive: carousel.active,
        displayOrder: carousel.displayOrder
      };
      this.dialogVisible = true;
    },
    handleFileChange(file) {
      this.form.file = file.raw;
    },
    resetForm() {
      this.form = {
        title: '',
        file: null,
        isActive: true,
        displayOrder: 0
      };
    },
    submitForm() {
      const token = localStorage.getItem('token');
      if (!token) {
        ElMessage.error('请先登录');
        this.$router.push('/admin-login');
        return;
      }

      const formData = new FormData();
      formData.append('title', this.form.title);
      if (this.form.file) {
        formData.append('file', this.form.file);
      }
      formData.append('isActive', this.form.isActive);
      formData.append('displayOrder', this.form.displayOrder);

      const request = this.isEditMode
        ? axios.put(`http://localhost:8080/api/admin/carousel/${this.currentId}`, formData, {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'multipart/form-data'
            }
          })
        : axios.post('http://localhost:8080/api/admin/carousel', formData, {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'multipart/form-data'
            }
          });

      request.then(() => {
        ElMessage.success(this.isEditMode ? '更新成功' : '添加成功');
        this.dialogVisible = false;
        this.fetchCarousels();
      })
      .catch(error => {
        ElMessage.error(error.response?.data?.message || '操作失败');
        console.error(error);
      });
    },
    deleteCarousel(id) {
      const token = localStorage.getItem('token');
      if (!token) {
        ElMessage.error('请先登录');
        this.$router.push('/admin-login');
        return;
      }

      this.$confirm('确定删除该轮播图吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.delete(`http://localhost:8080/api/admin/carousel/${id}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        .then(() => {
          ElMessage.success('删除成功');
          this.fetchCarousels();
        })
        .catch(error => {
          ElMessage.error('删除失败');
          console.error(error);
        });
      });
    }
  }
};
</script>

<style scoped>
.el-table {
  margin-top: 20px;
}

.el-image {
  border-radius: 4px;
}
</style>
