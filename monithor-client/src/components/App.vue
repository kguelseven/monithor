<template>
  <div id="app">
    <navigation :bus="bus"></navigation>
    <router-view :bus="bus"></router-view>
  </div>
</template>

<script>
  import Vue from 'vue'
  import Navigation from './Navigation'

  export default {
    name: 'app',
    data () {
      return {
        bus: new Vue()
      }
    },
    components: {
      Navigation
    }
  }
</script>

<style scoped>
</style>
