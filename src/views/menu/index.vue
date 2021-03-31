<template>
  <div class="app-container">
    <el-row>
      <el-col :span="6">
        <el-button size="mini" type="primary" @click="changeIsAdd" style="margin-bottom: 5px">新增模块</el-button>
        <el-tree
          :props="props"
          :data="menuList"
          node-key="id"
          :allow-drop="allowDrop"
          draggable
          :highlight-current="true"
          :accordion="false"
          :default-expand-all="false"
          :expand-on-click-node="false"
          @node-click="getClickMenu"
          @node-drag-start="handleDragStart"
          @node-drag-enter="handleDragEnter"
          @node-drag-leave="handleDragLeave"
          @node-drag-over="handleDragOver"
          @node-drag-end="handleDragEnd"
          @node-drop="handleDrop"
        />
      </el-col>
      <el-col :span="12">
        <el-form
          ref="form"
          :model="form"
          label-width="120px"
        >
          <el-form-item label="菜单名称">
            <el-input v-model="form.name"/>
          </el-form-item>
          <el-form-item label="图标">
            <el-input v-model="form.icon"/>
          </el-form-item>
          <el-form-item label="路径">
            <el-input v-model="form.path"/>
          </el-form-item>
          <el-form-item
            v-show="form.children.length === 0"
            label="组件路径"
          >
            <el-input v-model="form.component"/>
          </el-form-item>
          <el-form-item
            v-show="form.children.length !== 0"
            label="重定向路径"
          >
            <el-input v-model="form.redirect"/>
          </el-form-item>
          <el-form-item label="是否显示">
            <el-radio-group v-model="form.hidden">
              <el-radio :label="false">显示</el-radio>
              <el-radio :label="true">隐藏</el-radio>
            </el-radio-group>

          </el-form-item>
          <el-form-item>
            <el-button v-if="!isAdd" type="primary" @click="update">修改</el-button>
            <el-button v-else type="primary" @click="insert">新增</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { insert, listMenu, update } from '@/api/menu'

export default {
  name: 'Menu',
  data() {
    return {
      isAdd: true,
      menuList: [],
      props: {
        label: 'name',
        children: 'children'
      },
      form: {
        id: '',
        name: '',
        path: '',
        icon: '',
        component: '',
        redirect: '',
        hidden: false,
        children: []
      }
    }
  },
  beforeMount() {
    this.listMenu()
  },
  methods: {
    insert() {
      insert(this.form).then(response => {
        const { code } = response
        if (code === 200) {
          this.listMenu()
          this.$message.success('新增成功,刷新页面后生效')
        } else {
          this.$message.error('新增失败')
        }
      })
    },
    update() {
      update({ menuList: JSON.stringify([this.form]) }).then(response => {
        const { code } = response
        if (code === 200) {
          this.$message.success('修改成功,刷新页面后生效')
        } else {
          this.$message.error('修改失败')
        }
      })
    },
    changeIsAdd() {
      this.isAdd = true
      this.resetForm()
    },
    resetForm() {
      this.form = {
        name: '',
        path: '',
        icon: '',
        component: '',
        children: []
      }
    },
    getClickMenu(menu) {
      console.log(menu)
      this.form = menu
      this.isAdd = false
    },
    listMenu() {
      listMenu().then((resp) => {
        const { data } = resp
        this.menuList = data
      })
    },
    handleDragStart(node, ev) {
      console.log('drag start', node)
    },
    handleDragEnter(draggingNode, dropNode, ev) {
      console.log('tree drag enter: ', dropNode.label)
    },
    handleDragLeave(draggingNode, dropNode, ev) {
      console.log('tree drag leave: ', dropNode.label)
    },
    handleDragOver(draggingNode, dropNode, ev) {
      console.log('tree drag over: ', dropNode.label)
    },
    handleDragEnd(draggingNode, dropNode, dropType, ev) {
      console.log('tree drag end: ', dropNode && dropNode.label, dropType)
    },
    handleDrop(draggingNode, dropNode, dropType, ev) {
      let draggingNodeData = draggingNode.data
      let dropNodeData = dropNode.data
      if (dropType == 'inner') {
        draggingNodeData.parentId = dropNodeData.id
      } else {
        draggingNodeData.parentId = dropNodeData.parentId
        if (dropType == 'after') {
          draggingNodeData.sort = dropNodeData.sort + 1
        } else if (dropType == 'before') {
          draggingNodeData.sort = dropNodeData.sort - 1
        }
      }

      update({ menuList: JSON.stringify([draggingNodeData, dropNodeData]) })
    },
    allowDrop(draggingNode, dropNode, type) {
      return true
    }
  }
}
</script>

<style scoped>

</style>
