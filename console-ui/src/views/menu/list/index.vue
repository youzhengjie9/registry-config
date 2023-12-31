<template>
  <div class="manage">
    <!-- 新增/修改菜单的dialog -->
    <el-dialog
      :title="operateType === 'add' ? '新增菜单' : '修改菜单'"
      :visible.sync="openAddOrModifyMenuDialog"
    >
      <!-- 所属菜单内层dialog -->
      <el-dialog
        width="35%"
        title="所属菜单"
        :visible.sync="selectParentMenuShow"
        append-to-body
      >
        <!-- 树形控件单选 -->
        <el-tree
          :data="menuTreeData"
          ref="tree"
          :props="defaultProps"
          node-key="id"
          default-expand-all
          show-checkbox
          check-strictly
          highlight-current
          :default-checked-keys="menuTreeDefaultCheckedArray"
          @check-change="handleNodeClick"
        />
      </el-dialog>

      <!-- 选择菜单图标内层dialog -->
      <el-dialog
        width="35%"
        title="选择菜单图标"
        :visible.sync="selectMenuIconShow"
        append-to-body
      >
        <div style="height: 170px; overflow: auto">
          <!-- 生成icon -->
          <el-col
            :span="3"
            style="margin-right: 20px; margin-bottom: 15px"
            v-for="(iconName, index) in iconData"
            :key="index"
            @click.native="selectIconHandle(iconName)"
          >
            <el-card shadow="hover">
              <i :class="iconName" style="font-size: 25px"></i>
            </el-card>
          </el-col>
        </div>
      </el-dialog>

      <!-- dialog的内容 -->
      <el-radio-group
        class="redioGroup"
        v-model="operateForm.radio"
        @change="radioChange"
        v-if="this.operateType === 'add'"
      >
        <el-radio :label="0">目录</el-radio>
        <el-radio :label="1">菜单</el-radio>
        <el-radio :label="2">按钮</el-radio>
      </el-radio-group>
      <el-form ref="form" label-width="100px" :model="operateForm" inline>
        <el-form-item label="所属菜单">
          <el-input
            v-model="this.selectNode.menuName"
            readonly
            @focus="openSelectParentMenu"
          >
          </el-input>
        </el-form-item>

        <el-form-item label="菜单名称">
          <el-input v-model="operateForm.menuName"> </el-input>
        </el-form-item>

        <el-form-item label="路由地址" v-if="operateForm.radio === 1">
          <el-input v-model="operateForm.path"> </el-input>
        </el-form-item>

        <el-form-item label="组件路径" v-if="operateForm.radio === 1">
          <el-input v-model="operateForm.component"> </el-input>
        </el-form-item>

        <el-form-item label="菜单状态">
          <el-switch v-model="operateForm.status"> </el-switch>
        </el-form-item>

        <el-form-item label="菜单显示状态" v-if="operateForm.radio !== 2">
          <el-switch v-model="operateForm.visible"> </el-switch>
        </el-form-item>

        <el-form-item label="权限标识" v-if="operateForm.radio !== 0">
          <el-input v-model="operateForm.perms"> </el-input>
        </el-form-item>

        <el-form-item label="菜单图标" v-if="operateForm.radio !== 2">
          <el-input
            v-model="this.selectIconName"
            readonly
            @focus="openSelectIcon"
          >
          </el-input>
        </el-form-item>

        <el-form-item label="菜单排序号">
          <el-input v-model="operateForm.sort"> </el-input>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="operateForm.remark" type="textarea"> </el-input>
        </el-form-item>
      </el-form>

      <!-- dialog的底部 -->
      <div slot="footer" class="dialog-footer">
        <el-button @click="openAddOrModifyMenuDialog = false">取消</el-button>
        <el-button type="primary" @click="confirm">确定</el-button>
      </div>
    </el-dialog>

    <!-- 表格的头部,例如新增、导入 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-button v-hasPerm="['menu:add']" type="primary" @click="addMenu"
          >+ 新增</el-button
        >
        <el-button type="primary" @click="exportAllMenu"> 导出</el-button>
      </el-col>
    </el-row>

    <!-- 菜单的表格内容 -->
    <el-table
      :data="tableData"
      style="width: 100%; margin-bottom: 20px"
      row-key="id"
      border
      stripe
      default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      :header-cell-style="{ background: '#eef1f6', color: '#606266' }"
      v-loading="tableLoading"
      element-loading-background="rgba(255, 255, 255, .5)"
      element-loading-text="拼命加载中"
      element-loading-spinner="el-icon-loading"
    >
      <el-table-column
        prop="menuName"
        label="菜单名称"
        width="180"
        show-overflow-tooltip
      >
      </el-table-column>

      <el-table-column prop="icon" label="图标" width="53">
        <!-- 使用slot将默认展示文本的机制替换成展示icon -->
        <template slot-scope="scope">
          <i :class="scope.row.icon"></i>
        </template>
      </el-table-column>

      <el-table-column
        prop="type"
        label="菜单类型"
        width="78"
        filter-placement="bottom-end"
      >
        <template slot-scope="scope">
          <!-- 目录类型 -->
          <el-tag v-if="scope.row.type === 0" type="primary" disable-transitions
            >目录
          </el-tag>
          <!-- 菜单类型 -->
          <el-tag v-if="scope.row.type === 1" type="success" disable-transitions
            >菜单
          </el-tag>
          <!-- 按钮类型 -->
          <el-tag v-if="scope.row.type === 2" type="danger" disable-transitions
            >按钮
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        prop="path"
        label="路由地址"
        width="105"
        show-overflow-tooltip
      >
      </el-table-column>

      <el-table-column
        prop="component"
        label="组件路径"
        width="220"
        show-overflow-tooltip
      >
      </el-table-column>

      <el-table-column
        prop="status"
        label="菜单状态"
        width="78"
        filter-placement="bottom-end"
      >
        <template slot-scope="scope">
          <!-- 菜单状态 -->
          <el-tag
            v-if="scope.row.status === 0"
            type="success"
            disable-transitions
            >正常
          </el-tag>
          <!-- 菜单状态 -->
          <el-tag
            v-if="scope.row.status === 1"
            type="danger"
            disable-transitions
            >停用
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        prop="visible"
        label="菜单是否显示"
        width="100"
        filter-placement="bottom-end"
      >
        <template slot-scope="scope">
          <!-- 目录类型 -->
          <el-tag
            v-if="scope.row.visible === 0"
            type="success"
            disable-transitions
            >显示
          </el-tag>
          <!-- 菜单类型 -->
          <el-tag
            v-if="scope.row.visible === 1"
            type="danger"
            disable-transitions
            >隐藏
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        prop="perms"
        label="权限标识"
        width="150"
        show-overflow-tooltip
      >
      </el-table-column>

      <el-table-column
        prop="sort"
        label="排序"
        width="73"
        sortable
        show-overflow-tooltip
      >
      </el-table-column>

      <el-table-column label="操作" min-width="180">
        <template slot-scope="scope">
          <el-button
            v-hasPerm="['menu:modify']"
            size="mini"
            type="primary"
            @click="editMenu(scope.row)"
            >编辑</el-button
          >
          <el-button
            v-hasPerm="['menu:delete']"
            size="mini"
            type="danger"
            @click="delMenu(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import {
  addMenu,
  modifyMenu,
  deleteMenu,
  getMenuNameByMenuId,
} from "@/api/menu";
import {
  buildCanChooseMenuTreeByNewMenuType,
  buildAllMenuPermissionTree,
} from "@/api/menuTree";

//要写成这种{}的形式导入export-excel，否则将导出不了
import { exportAllMenu } from "@/api/exportExcel";

export default {
  name: "MenuList",
  data() {
    return {
      //判断新增还是修改
      operateType: "add",
      //true则打开新增或者修改的表单dialog，false则关闭
      openAddOrModifyMenuDialog: false,
      //内层打开选择所属菜单选项dialog
      selectParentMenuShow: false,
      //内层打开选择菜单图标选项dialog
      selectMenuIconShow: false,
      //所属菜单树默认选中数组
      menuTreeDefaultCheckedArray: [],
      //图标icon数据（目前只支持elementui的icon）
      iconData: [
        "el-icon-platform-eleme",
        "el-icon-eleme",
        "el-icon-delete-solid",
        "el-icon-delete",
        "el-icon-s-tools",
        "el-icon-setting",
        "el-icon-user-solid",
        "el-icon-user",
        "el-icon-phone",
        "el-icon-phone-outline",
        "el-icon-star-off",
        "el-icon-s-goods",
        "el-icon-warning",
        "el-icon-warning-outline",
        "el-icon-question",
        "el-icon-info",
        "el-icon-remove",
        "el-icon-circle-plus",
        "el-icon-success",
        "el-icon-error",
        "el-icon-zoom-in",
        "el-icon-zoom-out",
        "el-icon-remove-outline",
        "el-icon-circle-plus-outline",
        "el-icon-circle-check",
        "el-icon-circle-close",
        "el-icon-s-help",
        "el-icon-help",
        "el-icon-picture",
        "el-icon-picture-outline",
        "el-icon-picture-outline-round",
        "el-icon-upload",
        "el-icon-upload2",
        "el-icon-download",
        "el-icon-camera-solid",
        "el-icon-camera",
        "el-icon-video-camera-solid",
        "el-icon-video-camera",
        "el-icon-message-solid",
        "el-icon-bell",
        "el-icon-s-cooperation",
        "el-icon-s-order",
        "el-icon-s-platform",
        "el-icon-s-fold",
        "el-icon-s-unfold",
        "el-icon-s-promotion",
        "el-icon-s-home",
        "el-icon-s-marketing",
        "el-icon-s-comment",
        "el-icon-s-claim",
        "el-icon-s-custom",
        "el-icon-s-data",
        "el-icon-view",
        "el-icon-chat-dot-round",
      ],
      //当新增/修改时，表单数据就会同步到这里
      operateForm: {
        //当前单选框选中的label
        radio: 2,
        parentId: "",
        menuName: "aaa",
        path: "",
        component: "",
        status: true,
        visible: true,
        perms: "",
        icon: "",
        sort: "",
        remark: "",
      },

      searchForm: {
        keyword: "",
      },
      //所属菜单的数据
      menuTreeData: [],
      //所属菜单选中的节点
      selectNode: {},
      //选择图标选中图标名
      selectIconName: "",
      //表格数据
      tableData: [],
      // 表格是否加载中（ true说明表格正在加载中,则会显示加载动画。反之false则关闭加载动画）
      tableLoading: false,
      //所属菜单dialog中的树形控件所需要的配置
      defaultProps: {
        //子节点数组名（我们后台查出来的子节点数组名为children）
        children: "children",
        //所属菜单dialog中的树形控件每一行的展示的名字（我们后台查的每一项名字为menuName）
        label: "menuName",
      },
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    // 加载数据
    loadData() {
      this.tableLoading = true;
      // 模拟延迟,让加载动画更明显
      setTimeout(() => {
        buildAllMenuPermissionTree().then((response) => {
          let result = response.data;
          this.tableData = JSON.parse(result.data);
          // 关闭表格的加载动画
          this.tableLoading = false;
        });
      }, 500);
    },
    confirm() {
      //处理一些数据
      let menuForm = this.operateForm;
      //status
      if (menuForm.status === true) {
        menuForm.status = 0;
      } else {
        menuForm.status = 1;
      }
      //visible
      if (menuForm.visible === true) {
        menuForm.visible = 0;
      } else {
        menuForm.visible = 1;
      }

      //封装数据
      let menuDTO = {
        menu: menuForm,
        parentId: this.selectNode.id,
        menuType: this.operateForm.radio,
      };
      if (this.operateType === "add") {
        addMenu(menuDTO)
          .then((response) => {
            if (response.data.code == 200) {
              this.$message({
                showClose: true,
                message: "添加成功",
                type: "success",
                duration: 1000,
              });
              this.openAddOrModifyMenuDialog = false;
              this.loadData();
            } else {
              this.$message({
                showClose: true,
                message: "添加失败",
                type: "error",
                duration: 1000,
              });
            }
          })
          .catch((err) => {
            this.$message({
              showClose: true,
              message: "添加失败",
              type: "error",
              duration: 1000,
            });
          });
      } else {
        modifyMenu(menuDTO)
          .then((response) => {
            if (response.data.code == 200) {
              this.$message({
                showClose: true,
                message: "修改成功",
                type: "success",
                duration: 1000,
              });
              this.openAddOrModifyMenuDialog = false;
              this.loadData();
            } else {
              this.$message({
                showClose: true,
                message: "修改失败",
                type: "error",
                duration: 1000,
              });
            }
          })
          .catch((err) => {
            this.$message({
              showClose: true,
              message: "修改失败",
              type: "error",
              duration: 1000,
            });
          });
      }
    },
    //新增菜单
    addMenu() {
      this.openAddOrModifyMenuDialog = true;
      this.operateType = "add";
      //初始化operateForm数据
      this.operateForm = {
        radio: 0,
        parentId: "",
        menuName: "",
        path: "",
        component: "",
        status: true,
        visible: true,
        perms: "",
        icon: "",
        sort: "",
        remark: "",
      };
      //清空所属菜单选中的节点
      this.selectNode = {};
      //清空menuTreeDefaultCheckedArray
      this.menuTreeDefaultCheckedArray = [];
      //清空之前选择过的图标
      this.selectIconName = "";
    },
    //修改菜单
    editMenu(row) {
      this.operateType = "edit";
      this.openAddOrModifyMenuDialog = true;
      this.operateForm = row;
      //对数据进行处理
      if (this.operateForm.status === 0) {
        this.operateForm.status = true;
      } else {
        this.operateForm.status = false;
      }
      if (this.operateForm.visible === 0) {
        this.operateForm.visible = true;
      } else {
        this.operateForm.visible = false;
      }
      //输入框展示icon
      this.selectIconName = this.operateForm.icon;

      this.operateForm.radio = this.operateForm.type;
      //将this.operateForm.parentId转成菜单名称
      getMenuNameByMenuId(this.operateForm.parentId).then((res) => {
        //错误案例：使用这种方法不会让v-model实时更新
        // this.selectNode.menuName=res.data.data
        //正确案例：
        this.selectNode = {
          id: this.operateForm.parentId,
          menuName: res.data.data,
        };
        this.$forceUpdate(); //强制刷新v-model
      });
    },
    //删除菜单
    delMenu(row) {
      this.$confirm("是否删除该菜单？", "提示", {
        confirmButtonText: "确认",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        const menuid = row.id;
        deleteMenu(menuid)
          .then((response) => {
            if (response.data.code == 200) {
              this.$message({
                showClose: true,
                type: "success",
                message: "删除成功",
              });
              //刷新数据
              this.loadData();
            } else {
              this.$message({
                showClose: true,
                type: "error",
                message: "删除失败",
              });
            }
          })
          .catch((err) => {
            this.$message({
              showClose: true,
              type: "error",
              message: "删除失败",
            });
          });
      });
    },
    radioChange(val) {
      //当按钮发生变化，下面所有框内的值也要清空（还原）
      this.operateForm = {
        radio: val,
        parentId: "",
        menuName: "",
        path: "",
        component: "",
        status: true,
        visible: true,
        perms: "",
        icon: "",
        sort: "",
        remark: "",
      };

      //清空所属菜单选中的节点
      this.selectNode = {};
      //清空menuTreeDefaultCheckedArray
      this.menuTreeDefaultCheckedArray = [];
      //清空之前选择过的图标
      this.selectIconName = "";
    },
    //打开选择所属菜单选项dialog
    openSelectParentMenu() {
      this.selectParentMenuShow = true;

      //加载所属菜单的数据
      buildCanChooseMenuTreeByNewMenuType(this.operateForm.radio).then(
        (response) => {
          this.menuTreeData = JSON.parse(response.data.data);

          //通过ES6的Object.keys()方法来判断对象是否是空对象，如果不是空对象则设置默认选中
          if (Object.keys(this.selectNode).length !== 0) {
            //设置默认选中
            this.menuTreeDefaultCheckedArray.push(this.selectNode.id);
          }
        }
      );
    },
    //菜单树形控件单选
    handleNodeClick(data, checked, node) {
      if (checked) {
        this.$refs.tree.setCheckedNodes([data]);
        this.selectNode = data;
        //当选择其他节点则先清空menuTreeDefaultCheckedArray，再push新的节点
        this.menuTreeDefaultCheckedArray = [];
        this.menuTreeDefaultCheckedArray.push(data.id);
      }
    },
    openSelectIcon() {
      this.selectMenuIconShow = true;
    },
    //点击选择图标之后的回调
    selectIconHandle(iconName) {
      //把选择的图标存起来，便于输入框展示
      this.selectIconName = iconName;
      this.selectMenuIconShow = false;
      //把选择图标存起来，用于上传给服务器接口
      this.operateForm.icon = iconName;
    },
    //导出所有菜单
    exportAllMenu() {
      this.$confirm("是否导出所有菜单的数据?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          //调用导出所有菜单信息api
          exportAllMenu()
            .then((response) => {
              // -----导出excel固定写法，直接复制即可-----
              let disposition = response.headers["content-disposition"];
              let filename = disposition
                ? disposition.substring(
                    disposition.indexOf("=") + 1,
                    disposition.indexOf(".")
                  )
                : "下载文件";
              let newName = decodeURI(escape(filename));
              let extName = disposition.substring(disposition.indexOf(".") + 1);
              let blob = new Blob([response.data], {
                type: "application/vnd.ms-excel",
              });
              let link = document.createElement("a");
              let evt = document.createEvent("HTMLEvents");
              evt.initEvent("click", false, false);
              link.href = URL.createObjectURL(blob);
              link.download = newName + "." + extName;
              link.style.display = "none";
              document.body.appendChild(link);
              link.click();
              window.URL.revokeObjectURL(link.href);
            })
            .catch((err) => {
              this.$message({
                showClose: true,
                message: "导出excel失败",
                type: "error",
                duration: 1000,
              });
            });
        })
        .catch((err) => {});
    },
  },
};
</script>
<style lang="less" scoped>
.redioGroup {
  height: 35px;
  margin-left: 30px;
}
</style>
  