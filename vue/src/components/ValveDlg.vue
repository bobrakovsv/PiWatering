<template>
  <v-dialog scrollable persistent v-model="dialog">
    <v-card>
      <v-card-title>
        <span v-if="id != null">Настройка {{ valve.NAME }}</span>
        <span v-else>Добавление крана</span>
        <v-spacer/>
        <v-btn class="ml-2" small icon @click="cancel">
          <v-icon>mdi-close-box</v-icon>
        </v-btn>

      </v-card-title>
      <v-card-text>
        <v-text-field
            v-model="valve.NAME"
            label="Обозначение"
            hint="Краткое обозначение крана"
            required
            filled
        />
        <v-text-field
            v-model="valve.DESCR"
            label="Описние"
            hint="Краткое описание крана"
            required
            filled
        />
        <v-textarea
            v-model="valve.OPEN_DURATION_EXPR"
            label="Длительность открытия, мин"
            hint="Длительность в формате числового выражения СУБД H2 (можно просто вписать целое число). Кран автоматически закроется после данного интервала"
            required
            auto-grow
            dense
            filled
            rows="1"
        />
        <v-switch
            v-model="openByCondition"
            class="ma-0 pa-0"
            :label="(openByCondition === true ? 'Открывать по условию' : 'Открывать после закрытия другого крана')"
            color="secondary"
        />
        <v-select
            v-model="valve.OPEN_AFTER_ID"
            class="ma-0 pa-0"
            label="Открывать после"
            :items="valveList"
        />
        <v-textarea
            v-model="valve.OPEN_CONDITION_EXPR"
            v-if="openByCondition"
            label="Условие открытия"
            hint="Условие в формате булевского выражения СУБД H2 при выполнении которого кран автоматически открывается.
                           Допустимо использовать функции: ЧАС(), МИН(), ВРЕМЯ(), ДОЖДЬ(Ч), ВЕТЕР(Ч), ТЕМП(Ч), ТЕМП_МАКС(Ч), ТЕМП_МИН(Ч).
                           Пример: время()='19:30' and дождь(-24)<3 and дождь(24)<3 and темп_мин(24)>15"
            persistent-hint
            required
            auto-grow
            dense
            filled
            rows="1"
        />
      </v-card-text>

      <v-card-actions>
        <v-spacer/>
        <v-btn class="mx-2" color="primary" @click="save">
          <v-icon>mdi-content-save</v-icon>
        </v-btn>
      </v-card-actions>

    </v-card>
  </v-dialog>
</template>

<script>
export default {
  data: () => ({
    valve: {},
    dialog: false,
    id: null,
    openByCondition: true,
    valveList: [],
  }),

  created() {
    document.title = "Настройка крана";
  },

  methods: {
    // Обновление данных
    fetchData() {
      return this.$root.restGet('/valve/' + this.id)
          .then((res) => {
            this.valve = res
            this.openByCondition = (this.valve.OPEN_AFTER_ID == null)
            this.fetchListData()
          })
    },

    // Получение данных для списка Открывать после
    fetchListData() {
      return this.$root.restGet('/valve/list')
          .then((res) => {
            for (let i = 0; i < res.length; i++) {
              if (res[i].ID != this.id) {
                this.valveList.push({
                  value: res[i].ID,
                  text: res[i].NAME
                })
              }
            }
          })
    },

    // Сохранить изменения
    save() {
      if (this.openByCondition === true)
        this.valve.OPEN_AFTER_ID = null
      else
        this.valve.OPEN_CONDITION_EXPR = null
      this.$root.restPost('/valve', this.valve)
          .then((res) => {
            this.id = res.ID
            this.fetchData()
            this.$root.showSucc()
          })
    },

    // Закрыть диалог
    cancel() {
      this.dialog = false
    },

    open(id) {
      this.id = id
      this.valve = {}
      this.fetchData()
      this.dialog = true
    },

  },

}
</script>
