<template>
  <div class="container form-horizontal pull-left">
    <form>
      <div class="form-group">
        <label class="control-label col-sm-2"></label>
        <div class="col-sm-10">
          <h1 v-if="this.job.id">Edit Job</h1>
          <h1 v-if="!this.job.id">Add Job</h1>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-2" for="formJobName">Job Name</label>
        <div class="col-sm-10" :class="{ 'has-error': errors.has('jobName')}">
          <input class="form-control" id="formJobName" name="jobName" v-model="job.name" v-validate
                 data-vv-rules="required">
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-2" for="formJobInterval">Interval in secs</label>
        <div class="col-sm-2" :class="{ 'has-error': errors.has('jobInterval')}">
          <input class="form-control" id="formJobInterval" name="jobInterval" placeholder="300"
                 v-model="job.intervalSecs" v-validate data-vv-rules="required|numeric">
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-2" for="formJobUrl">URL</label>
        <div class="col-sm-10" :class="{ 'has-error': errors.has('jobUrl') }">
          <input class="form-control" id="formJobURL" name="jobUrl" placeholder="http://.." v-model="job.url"
                 v-validate data-vv-rules="required|url">
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-2" for="formJobMatch">Match</label>
        <div class="form-inline col-sm-10" :class="{ 'has-error': errors.has('jobMatch')}">
          <input class="form-control" style="width: 100%" id="formJobMatch" name="jobMatch"
                 placeholder="sbb access" v-model="job.successMatch" v-validate data-vv-rules="required">
        </div>
      </div>
      <hr>
      <div class="form-group">
        <label class="control-label col-sm-2"></label>
        <div class="col-sm-10">
          <strong>Optional</strong>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label  col-sm-2" for="formJobTags">Tags</label>
        <div class="col-sm-10">
          <input class="form-control" placeholder="A,B,C.." id="formJobTags" v-model="tags">
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-2" for="formJobVersionMatch">Overwrite Version</label>
        <div class="form-inline col-sm-10">
          <input class="form-control" style="width: 100%" id="formJobVersionMatch" name="jobVersionMatch"
                 placeholder=".*?(\d+\.\d+\.\d+(-SNAPSHOT)?).*" v-model="job.versionMatch">
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-2" for="formJobBuildTimestampMatch">Overwrite Build Timestamp</label>
        <div class="form-inline col-sm-10">
          <input class="form-control" style="width: 100%" id="formJobBuildTimestampMatch" name="jobBuildTimestampMatch"
                 placeholder=".*?(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}).*" v-model="job.buildTimestampMatch">
        </div>
      </div>
      <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
          <button v-if="this.$route.params.id" class="btn btn-primary" @click.prevent="cancelJob()" >
            Go Back
          </button>
          <button class="btn btn-primary" :disabled="formFields.failed()" @click.prevent="saveJob()">
            <span v-if="this.job.id">Update</span>
            <span v-else>Add</span>
          </button>
        </div>
      </div>
    </form>
    <hr>
    <div class="form-group">
      <label class="control-label col-sm-2"></label>
      <div class="col-sm-10">
        <strong>Last result: {{lastTimestampFormatted}}</strong>
      </div>
    </div>
    <div class="form-group">
      <label class="control-label col-sm-2"></label>
      <div class="col-sm-10"></div>
      <pre>{{jobPrettyPrinted}}</pre>
    </div>

  </div>
</template>

<script>
  export default {
    name: 'job',
    props: {
      bus: {
        type: Object,
        required: true
      }
    },
    data () {
      return {
        job: {},
        jobPrettyPrinted: '',
        tags: '',
        message: ''
      }
    },
    computed: {
      lastTimestampFormatted () {
        if (this.job.lastTimestamp) {
          return new Date(this.job.lastTimestamp).toLocaleString()
        }
      }
    },
    beforeRouteEnter (to, from, next) {
      next(vm => {
        if (vm.$route.params.id) {
          vm.loadJob(vm.$route.params.id)
        } else {
          vm.initJob()
        }
      })
    },
    methods: {
      initJob () {
        this.job = {intervalSecs: 300, successMatch: 'sbb access'}
        this.jobPrettyPrinted = ''
        this.tags = ''
        this.message = ''
      },
      saveJob () {
        if (this.$validator.validateAll()) {
          if (this.tags) {
            this.job.tags = this.tags.replace(' ', '').split(',')
          }
          this.$http.post('/jobs/', this.job).then((response) => {
            this.bus.$emit('message', 'Job saved')
            this.loadJob(response.body.id)
          }, (response) => {
            this.bus.$emit('error', response.statusText, response.status)
          })
        }
      },
      loadJob (id) {
        this.$http.get('/jobs/' + id).then((response) => {
          this.job = response.body
          this.jobPrettyPrinted = JSON.stringify(this.job, null, 2)
          if (this.job.tags) {
            this.tags = this.job.tags.join(',')
          }
        }, (response) => {
          this.bus.$emit('error', response.statusText, response.status)
        })
      },
      cancelJob () {
        if (this.$route.params.id) {
          this.$router.go(-1)
        } else {
          this.initJob()
        }
      }
    }
  }
</script>
<style scoped>
</style>
