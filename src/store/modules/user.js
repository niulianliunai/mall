import { login, logout, getInfo, getMenu } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router, { constantRoutes, resetRouter } from '@/router'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    avatar: ''
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  }
}

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password: password }).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        setToken(data.token)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token).then(response => {
        const { data } = response

        if (!data) {
          return reject('Verification failed, please Login again.')
        }

        const { id, name, avatar } = data
        getMenu({ id, type: 0 }).then(response => {
          const { data } = response
          for (let item of data) {
            let menu = {
              path: item.path,
              name: item.name,
              meta: { title: item.name, icon: item.icon },
              component: (resolve) => require(['@/layout'], resolve),
              redirect: item.redirect ? item.redirect : undefined,
              hidden: item.hidden,
              children: []
            }
            constantRoutes.push(menu)
            handleChildren(menu, item.children)
          }

          // for (let child of data.children) {
          //   let menu1 = {
          //     path: child.path,
          //     name: child.name,
          //     component: (resolve) => require(['@/views' + child.component], resolve),
          //     redirect: child.redirect ? child.redirect : undefined,
          //     meta: { title: child.name, icon: child.icon },
          //   }
          //   menu.children.push(menu1)
          //   if (child.children.length>0) {
          //     menu1.children = []
          //     for (let child2 of child.children) {
          //       let menu2 = {
          //         path: child2.path,
          //         name: child2.name,
          //         component: (resolve) => require(['@/views' + child2.component], resolve),
          //         meta: { title: child2.name, icon: child2.icon }
          //       }
          //       menu1.children.push(menu2)
          //     }
          //   }
          // }
          // constantRoutes.push(menu)

          constantRoutes.push({ path: '*', redirect: '/404', hidden: true })
          resetRouter()
          router.push('')
        })

        commit('SET_NAME', name)
        commit('SET_AVATAR', avatar)

        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        removeToken() // must remove  token  first
        resetRouter()
        commit('RESET_STATE')
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken() // must remove  token  first
      commit('RESET_STATE')
      resolve()
    })
  }
}

function handleChildren(menu, children) {
  for (let child of children) {
    let childMenu = {
      path: child.path,
      name: child.name,
      meta: { title: child.name, icon: child.icon },
      component: (resolve) => require(['@/views' + child.component], resolve),
      hidden: child.hidden,
      children: []
    }
    menu.children.push(childMenu)
    handleChildren(childMenu, child.children)
    if (childMenu.children.length < 1) {
      childMenu.children = undefined
    }


  }

}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

