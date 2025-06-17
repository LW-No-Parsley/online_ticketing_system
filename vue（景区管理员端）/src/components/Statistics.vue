<template>
  <div class="statistics-container">
    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">¥ 256,800</div>
            <div class="stat-label">今日销售额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">1,248</div>
            <div class="stat-label">今日订单数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">3,562</div>
            <div class="stat-label">今日游客数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">82%</div>
            <div class="stat-label">入园率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mb-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>近7日销售趋势</span>
          </template>
          <div class="chart-container" style="height: 300px;">
            <!-- 这里将放置ECharts图表 -->
            <div class="chart-placeholder">销售趋势图表</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>票种销售比例</span>
          </template>
          <div class="chart-container" style="height: 300px;">
            <!-- 这里将放置ECharts图表 -->
            <div class="chart-placeholder">票种比例饼图</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>景区访问量排名</span>
          </template>
          <el-table :data="scenicRanking" style="width: 100%">
            <el-table-column prop="rank" label="排名" width="80" />
            <el-table-column prop="name" label="景区名称" />
            <el-table-column prop="visitors" label="访问量" />
            <el-table-column prop="growth" label="增长率">
              <template #default="scope">
                <el-tag :type="scope.row.growth >= 0 ? 'success' : 'danger'">
                  {{ scope.row.growth >= 0 ? '+' : '' }}{{ scope.row.growth }}%
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'Statistics',
  data() {
    return {
      scenicRanking: [
        { rank: 1, name: '黄山风景区', visitors: 5680, growth: 12.5 },
        { rank: 2, name: '九寨沟景区', visitors: 4320, growth: 8.3 },
        { rank: 3, name: '西湖景区', visitors: 3980, growth: -2.1 },
        { rank: 4, name: '故宫博物院', visitors: 3560, growth: 5.7 },
        { rank: 5, name: '长城景区', visitors: 2980, growth: 3.2 },
      ]
    }
  }
}
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  color: #999;
  font-size: 14px;
}

.chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.chart-placeholder {
  color: #999;
  font-size: 16px;
}
</style>
