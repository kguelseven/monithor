<template>
  <nav class="navbar-inverse navbar-fixed-top">
    <div>
      <span class="navbar-right logo"><a href="http://github.com/kguelseven/monithor"><img src="static/logo.png" alt="github" title="Monithor @github"></a></span>
      <form class="navbar-form navbar-left">
        <div class="form-group">
          <button type="button" class="btn btn-default btn-ml" @click="monitor()" title="Monitor"><span
                  class="glyphicon glyphicon-eye-open"></span></button>
          <button type="button" class="btn btn-default btn-ml" @click="newJob()" title="Add a new Job"><span
                  class="glyphicon glyphicon-plus"></span></button>
          <input type="text" class="form-control" placeholder="Name or Tag" v-model="query" ref="search">
          <button type="submit" class="btn btn-default" @click="searchJob()">Search</button>
        </div>
      </form>
      <transition name="fade">
        <span v-if="message" class="label label-default pull-right" style="font-size:14px;">{{message}}</span>
      </transition>
      <transition name="fade">
        <span v-if="error" class="label label-danger pull-right" style="font-size:14px;">{{error}}</span>
      </transition>
    </div>
  </nav>
</template>

<script>
  import router from '../router'

  export default {
    name: 'navigation',
    props: {
      bus: {
        type: Object,
        required: true
      }
    },
    data () {
      return {
        query: '',
        message: '',
        error: ''
      }
    },
    methods: {
      newJob () {
        router.push({name: 'new_job'})
      },
      searchJob () {
        router.push({name: 'jobs', query: {query: this.query}})
      },
      monitor () {
        router.push({name: 'monitor'})
      }
    },
    created () {
      this.bus.$on('message', (message) => {
        this.message = message
        setTimeout(() => { this.message = '' }, 3000)
      })
      this.bus.$on('error', (message, code) => {
        var text = message.substring(0, 50)
        if (code) {
          text = code + '.' + text
        }
        this.error = text
        setTimeout(() => { this.error = '' }, 3000)
      })
    }
  }
</script>
<style scoped>
span.label {
  margin: 5px;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity .5s
}
.fade-enter, .fade-leave-active {
  opacity: 0
}
.navbar-inverse {
  background-color: #424949;
}
.logo {
  margin: 0px;
  padding: 5px;
}
</style>
