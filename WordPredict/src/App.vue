<template>
  <a-row type="flex" justify="space-around" align="middle">
    <a-auto-complete
      v-model="value"
      :data-source="dataSource"
      style="width: 50%; margin-top: 200px;"
      placeholder="Anything..."
      size="large"
      @select="onSelect"
      @search="onSearch"
    >

    </a-auto-complete>
  </a-row>
</template>

<script>
let timeout
let currentValue

function startFetch (value, callback) {
  if (timeout) {
    clearTimeout(timeout)
    timeout = null
  }
  const prefix = value.substring(value.length - 2)
  currentValue = value

  function fake () {
    fetch('http://localhost:5000/suggest', {
      method: 'POST',
      mode: 'cors',
      body: JSON.stringify({
        prefix: prefix
      })
    }).then(res => res.json())
      .then(d => {
        if (currentValue === value) { // 忽略迟到的消息
          const result = d.data
          const data = []
          result.forEach(r => {
            data.push(prefix.substring(prefix.length - 1) + r)
          })
          console.log(data)
          callback(data)
        }
      })
  }

  timeout = setTimeout(fake, 250)
}

export default {
  data () {
    return {
      value: undefined,
      old_value: '',
      dataSource: []
    }
  },

  methods: {
    onSearch (value) {
      this.dataSource = []
      this.old_value = value // 维护旧值
      if (value.length < 2) return
      startFetch(value, data => (this.dataSource = data)) // 取最后两个字符进行预测
    },

    onSelect (value) {
      this.value = this.old_value + value.substring(value.length - 1)
      this.onSearch(this.value)
    }
  }

}
</script>

<style>
  body {
    background-color: #24292e
  };
</style>
