<template>
  <v-card class="mb-3" raised>
    <v-card-title><span class="primary--text mr-3">{{ valve.NAME }}</span>{{ valve.DESCR }}</v-card-title>

    <v-card-text>
      <div style="max-width:200pt">
        <v-simple-table>
          <thead>
          <tr>
            <th class="text-left">
              Время включения
            </th>
            <th class="text-middle">
              Длительность
            </th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="i in history"
              :key="i.ID"
          >
            <td>{{ i.openDate }}</td>
            <td>{{ i.duration }}</td>
          </tr>
          </tbody>
        </v-simple-table>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  props: {
    id: null,
  },

  data: () => ({
    valve: {},
    history: [],
  }),

  created() {
    document.title = "История состояний крана";
  },

  mounted() {
    this.fetchData();
  },

  methods: {
    // Обновление данных
    fetchData() {
      this.$root.restGet('/valve/' + this.id)
          .then(res => this.valve = res)
          .then(this.fetchHistoryData())
    },

    fetchHistoryData() {
      this.$root.restGet('/valve/' + this.id + '/history')
          .then(res => this.history = res)
    },

  }
}
</script>
