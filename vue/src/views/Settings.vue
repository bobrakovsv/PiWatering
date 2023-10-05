<template>
  <v-container>
    <v-card v-for="i in settings" :key="i.ID" class="mb-3" raised>
      <v-card-title>{{ i.NAME }}</v-card-title>

      <v-card-subtitle>
        <span class="pr-2">{{ i.DESCR }}</span>
      </v-card-subtitle>

      <v-card-text>
        <v-text-field
            v-model="i.VALUE"
            label="Значение"
            outlined
            placeholder=" "
        ></v-text-field>
      </v-card-text>
    </v-card>

    <v-bottom-navigation app>
      <v-btn class="ma-2" color="primary" @click="save">
        <v-icon>mdi-content-save</v-icon> Сохранить
      </v-btn>
    </v-bottom-navigation>

  </v-container>
</template>

<script>
export default {
  data: () => ({
    settings: [],
  }),

  created() {
    document.title = "Настройки";
  },

  mounted() {
    this.fetchData();
  },

  methods: {
    // Обновление данных
    fetchData() {
      return this.$root.restGet('/settings')
          .then((res) => {
            this.settings = res
          })
    },

    save() {
      this.$root.restPost('/settings', this.settings)
          .then(() => {
            this.fetchData()
            this.$root.showSucc()
          })
    },

  },
}
</script>
