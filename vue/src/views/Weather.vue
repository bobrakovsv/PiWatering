<template>
  <v-container>
    <v-simple-table>
      <thead>
      <tr>
        <th>Начало</th>
        <th>Окончание</th>
        <th>Темп., C</th>
        <th>Дождь, мм</th>
        <th>Ветер, м/сек</th>
        <th>Направление ветра, градусы</th>
        <th>Облачность, %</th>
      </tr>
      </thead>
      <tbody>
      <tr
          v-for="i in data"
          :key="i.ID"
      >
        <td>{{ i.START_DATE }}</td>
        <td>{{ i.END_DATE }}</td>
        <td>{{ i.TEMP }}</td>
        <td>{{ i.RAIN }}</td>
        <td>{{ i.WIND_SPEED }}</td>
        <td>{{ i.WIND_DEG }}</td>
        <td>{{ i.CLOUDS_PCT }}</td>
      </tr>
      </tbody>
    </v-simple-table>

    <v-bottom-navigation app>
      <v-btn @click="dateIncrement--">
        <v-icon dark>
          mdi-minus
        </v-icon>
      </v-btn>

      <v-btn>
        {{ date }}
      </v-btn>

      <v-btn class="mr-4" @click="dateIncrement++">
        <v-icon dark>
          mdi-plus
        </v-icon>
      </v-btn>

      <v-switch
          v-model="forecast"
          :label="`${ forecast ? 'Прогноз' : 'Наблюдения' }`"
          class="ml-5"
      />
    </v-bottom-navigation>

  </v-container>
</template>

<script>
export default {
  data: () => ({
    dateIncrement: 0,
    forecast: true,
    date: null,
    data: [],
  }),

  watch: {
    forecast() {
      this.fetchData()
    },
    dateIncrement() {
      this.fetchData()
    },
  },

  created() {
    document.title = "Погода"
  },

  mounted() {
    this.fetchData()
  },

  methods: {
    // Обновление данных
    fetchData() {
      this.$root.restGet('/weather?forecast=' + this.forecast + '&dateIncrement=' + this.dateIncrement)
          .then(res => {
            let d = res.data
            for (let i = 0; i < d.length; i++) {
              if (d[i].TEMP_MIN == d[i].TEMP_MAX) {
                d[i].TEMP = d[i].TEMP_MIN
              } else {
                d[i].TEMP = "от " + d[i].TEMP_MIN + " до " + d[i].TEMP_MAX
              }
              d[i].RAIN = Math.round(Number(d[i].RAIN) * 100) / 100
              if (isNaN(d[i].RAIN))
                d[i].RAIN = null
            }

            this.data = d
            this.date = res.date
          })
    },
  }
}
</script>
