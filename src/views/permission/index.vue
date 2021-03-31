<template>
  <div class="app-container">
    <el-table
      :data="permissionList"
      style="width: 100%">
      <el-table-column
        prop="id"
        label="序号"
        width="80">
      </el-table-column>
      <el-table-column
        prop="name"
        label="名称"
        width="180">
      </el-table-column>
      <el-table-column
        prop="path"
        label="路径"
        width="150"
      >
      </el-table-column>
      <el-table-column
        prop="description"
        label="注释"
        width="250"
      >
      </el-table-column>
      <el-table-column
        prop="createTime"
        label="创建时间"
        width="250"
      >
      </el-table-column>
      <el-table-column
        prop="updateTime"
        label="更新时间"
        width="250"
      >
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small">分配</el-button>
          <el-button @click="handleClick(scope.row)" type="text" size="small">修改</el-button>
          <el-button type="text" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { listPermission } from '@/api/permission'

export default {
  name: 'Permission',
  data() {
    return {
      permissionList: []
    }
  },
  beforeMount() {
    this.listPermission()
  },
  methods: {
    listPermission() {
      listPermission().then(response => {
        const { data } = response
        this.permissionList = data
      })
    }
  }
}
</script>

<style scoped>

</style>
