<template>
  <v-app id="inspire">
    <v-navigation-drawer
        v-model="drawer"
        app
        clipped
    >
      <v-list>
        <v-list-item v-for="i in menu" link :to="i.href" :key="i.href">
          <v-list-item-action>
            <v-icon>{{ i.icon }}</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title>{{ i.title }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-app-bar app clipped-left>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title>Автополив</v-toolbar-title>
    </v-app-bar>

    <v-content>
      <v-container fluid>
        <router-view id="router-view"/>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
import axios from 'axios'

export default {
  name: 'App',

  components: {},

  data: () => ({
    drawer: null,
    menu: [],
  }),

  created() {
    this.$vuetify.theme.dark = true

    this.$root.restGet = this.restGet
    this.$root.restPost = this.restPost
    this.$root.restPut = this.restPut
    this.$root.restDelete = this.restDelete

    this.$root.showErr = this.showErr
    this.$root.showInfo = this.showInfo
    this.$root.showSucc = this.showSucc
    this.$root.showWarning = this.showWarning

  },

  mounted() {
    this.makeMenu()
  },

  methods: {

    // Проверка ответа rest-сервиса. Если ответ вместо json установленной структуры содержит html с формой логина,
    // значит сессия завершена, и окно автоматически перенаправляется на страницу входа
    checkSessionExpire(restResponse) {
      if (restResponse.ok === undefined &&
          restResponse.data != null &&
          typeof (restResponse.data.indexOf) !== 'undefined' &&
          restResponse.data.indexOf('id="login-form"')
      ) {
        window.location.href = '/login'
      }
    },

    // Вызов рест-сервиса, возвращающего результат в стандартизованном виде,
    // с проверкой и отображением ошибки, с проверкой на истечение сессии и редиректом на страницу логина
    // Возвращается результат при отсутствии ошибки (ошибка отображается, но не возвращается)
    callRest(axiosMethod, url, payload) {
      var incorrectResponse = 'Неверный ответ сервера (' + url + ')'
      return new Promise((resolve, reject) => {
        axiosMethod(url, payload)
            .then(res => {
              this.checkSessionExpire(res)
              if (!res.hasOwnProperty('data') || !res.data.hasOwnProperty('ok')) {
                this.$root.showErr(incorrectResponse)
                reject(incorrectResponse)
                return
              }
              var r = res.data
              if (!r.ok) {
                this.$root.showErr(r.error)
                reject(r.error)
              } else {
                resolve(r.data)
              }
            })
            .catch(e => {
              console.log(e)
              let msg = 'Неизвестная ошибка'
              if (e != null) {
                if (e.response) {
                  // The request was made and the server responded with a status code
                  msg = e.message
                } else if (e.request) {
                  // The request was made but no response was received
                  msg = 'Нет связи с сервером'
                }
              }
              this.$root.showErr(msg)
              reject(e)
            })
      })
    },

    restGet(url) {
      return this.callRest(axios.get, url)
    },

    restPost(url, payload) {
      return this.callRest(axios.post, url, payload)
    },

    restPut(url, payload) {
      return this.callRest(axios.put, url, payload)
    },

    restDelete(url) {
      return this.callRest(axios.delete, url)
    },


    // Может использоваться всеми вложенными компонентами для отображения ошибок
    showErr(text, title) {
      this.$noty.error((title != null ? title : 'Ошибка'), text.replace("\n", "<br>"))
      console.log((title != null ? title : '') + text)
    },

    // Может использоваться всеми вложенными компонентами для отображения предупреждений
    showWarning(text, title) {
      this.$noty.warning((title != null ? title : 'Предупреждение'), text.replace("\n", "<br>"))
      console.log((title != null ? title : '') + text)
    },

    // Может использоваться всеми вложенными компонентами для отображения информационного уведомления
    showInfo(text, title) {
      this.$noty.info((title != null ? title : 'Информация'), text.replace("\n", "<br>"))
    },

    // Может использоваться всеми вложенными компонентами для отображения уведомления об успешно выполненной операции (текст опциональный)
    showSucc(text, title) {
      if (title == null && text == null)
        this.$noty.success('Операция выполнена успешно', null, {timeout: 2000})
      else if (title != null && text != null)
        this.$noty.success(title, text)
      else {
        var t = (text != null ? text : title)
        if (t.length <= 100)
          this.$noty.success(t, null)
        else
          this.$noty.success(null, t)
      }
    },

    // Формирование меню верхнего уровня
    async makeMenu() {
      var m = [];
      this.menu = m;
      m.push({href: '/valves', icon: 'mdi-ship-wheel', title: 'Управление'})
      m.push({href: '/settings', icon: 'mdi-wrench', title: 'Настройки'})
      m.push({href: '/weather', icon: 'mdi-weather-partly-rainy', title: 'Погода'})
    }
  },

}
</script>
