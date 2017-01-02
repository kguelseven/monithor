module.exports = {
  timeAgo (timestamp) {
    var seconds = Math.floor((new Date() - timestamp) / 1000)
    var interval = Math.floor(seconds / 31536000)
    if (interval > 0) {
      return interval + ' year'
    }
    interval = Math.floor(seconds / 2592000)
    if (interval > 0) {
      return interval + ' month'
    }
    interval = Math.floor(seconds / 86400)
    if (interval > 0) {
      return interval + ' day'
    }
    interval = Math.floor(seconds / 3600)
    if (interval > 0) {
      return interval + ' hour'
    }
    interval = Math.floor(seconds / 60)
    if (interval > 0) {
      return interval + ' min'
    }
    return Math.floor(seconds) + ' sec'
  }
}

