<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>

import router from '@/router'

export default {
  name: 'App',

  beforeCreate() {
    setTimeout(() => {
      this.pushEmpty()
    }, 100)
  },
  created() {
    setTimeout(() => {
      this.pushEmpty()
    }, 1000)
  },
  beforeMount() {
    setTimeout(() => {
      this.pushEmpty()
    }, 2000)
  },
  methods: {
    pushEmpty() {
      console.log(1)
      router.push('')
      console.log(2)
    }
  }
}
</script>
