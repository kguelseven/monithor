<template>
  <div id="monitor">
    <table class="status">
      <template v-for="tagResult in tagResults">
        <tr class="status" :class="[{success: tagResult.success}, {failure: !tagResult.success}]"
            valign="top">
          <td width="10%">
            <span class="label label-tag">{{tagResult.tag}}</span><br>
          </td>
          <td>
            <template v-for="job in tagResult.jobs">
              <a :href="job.url" target="_blank"><span class="jobStatus" :class="[{success: job.lastResult}, {failure: !job.lastResult}]">
                  <router-link :to="{ name: 'edit_job', params: { id: job.id }}">{{job.name}}<br>{{version(job)}}</router-link>
                  <br>{{ago(job.lastTimestamp)}}</span></a>
            </template>
          </td>
        </tr>
      </template>
    </table>
  </div>
</template>

<script>
  import utils from '../utils'

  export default {
    name: 'monitor',
    props: {
      bus: {
        type: Object,
        required: true
      }
    },
    data () {
      return {
        tags: [],
        tagResults: [],
        doPolling: true
      }
    },
    mounted () {
      this.loadTags()
    },
    beforeDestroy () {
      this.doPolling = false
    },
    methods: {
      loadTags () {
        this.$http.get('/tags/').then((response) => {
          this.tags = response.data
          this.tagResults = new Array(this.tags.length).fill({})
          this.loadStatus()
        }, (response) => {
          this.bus.$emit('error', response.statusText, response.status)
        })
      },
      loadStatus () {
        this.tags.forEach((tag, index) => {
          this.$http.get('/tags/status/' + tag).then((response) => {
            this.$set(this.tagResults, index, response.data)
          }, (response) => {
            this.bus.$emit('error', response.statusText, response.status)
          })
        })
        this.bus.$emit('message', 'Updated ' + new Date().toLocaleTimeString())
        if (this.doPolling) {
          setTimeout(() => this.loadStatus(), 10000)
        }
      },
      notEmpty (arr) {
        return (typeof arr !== 'undefined' && arr.length > 0)
      },
      ago (millis) {
        return utils.timeAgo(millis)
      },
      version (job) {
        return (job.lastVersion) ? job.lastVersion : '(Unknown)'
      }
    }
  }
</script>
<style scoped>
  #monitor {
    margin: 50px 5px 5px 5px;
    color: white;
  }
  #monitor a {
    color: white;
  }
  table.status {
    border-collapse: separate;
    border-spacing: 0em 0.5em;
    width: 100%;
  }
  td {
    padding:7px 5px 7px 5px;
    font-size:14px;
  }
  .jobStatus {
    padding: 5px;
    margin: 5px;
    border: 2px solid white;
    float:left;
    font-size:12px;
  }
  .jobStatus:hover {
    border: 2px solid #424949;
  }
  .success {
    background-color: #5cb85c;
  }
  .failure {
    background-color: #d9534f;
  }
  .label-tag {
    background-color: #424949;
    font-size:14px;
  }
</style>
