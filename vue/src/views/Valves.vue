<template>
  <div>
    <v-card v-for="i in valves" :key="i.ID"
            class="mb-3" raised
            :loading="pendingId ==i.ID"
    >

      <v-card-title>{{ i.NAME }}</v-card-title>

      <v-card-subtitle>
        <span class="pr-2">{{ i.DESCR }}</span>
        <template v-if="i.AUTO_CONTROL === true">
                    <span v-if="i.open_after_name" class="pr-2">
                        Открывается после {{ i.open_after_name }}
                    </span>
          <span v-if="i.OPEN_CONDITION_EXPR" class="pr-2">
                        Открывается по условию: '{{ i.OPEN_CONDITION_EXPR }}'
                    </span>
        </template>

      </v-card-subtitle>

      <v-card-text>
        <span v-if="i.IS_OPEN === true">Открыт</span>
        <span v-else>Закрыт</span>

        <span v-if="i.state_date">
                        с {{ i.state_date }}
                    </span>
        <span v-if="i.next_state_date">
                        по {{ i.next_state_date }}
                    </span>
      </v-card-text>

      <v-card-actions>
        <v-btn class="mx-2" fab small
               :color="i.IS_OPEN === true ? 'primary' : 'info'"
               :disabled="pendingId != null"
               @click="toggleState(i)"
        >
          <v-icon v-if="i.IS_OPEN === true">mdi-water</v-icon>
          <v-icon v-else>mdi-water-off</v-icon>
        </v-btn>

        <v-btn class="mx-2" fab small
               :color="i.AUTO_CONTROL === true ? 'info' : 'warning'"
               :disabled="pendingId != null"
               @click="toggleControl(i)"
        >
          <v-icon v-if="i.AUTO_CONTROL === true">mdi-format-text-variant</v-icon>
          <v-icon v-else>mdi-hand</v-icon>
        </v-btn>

        <v-btn class="mx-2" fab small color="info"
               :disabled="pendingId != null"
               @click="$router.push({name: 'valveHistory', params: {id: i.ID}})"
        >
          <v-icon>mdi-history</v-icon>
        </v-btn>

        <v-btn class="mx-2" fab small color="info"
               :disabled="pendingId != null"
               @click="$refs.valveDlg.open(i.ID)"
        >
          <v-icon>mdi-wrench</v-icon>
        </v-btn>
      </v-card-actions>

    </v-card>

    <ValveDlg ref="valveDlg"/>
  </div>
</template>

<script>
import ValveDlg from "@/components/ValveDlg";

export default {
  components: {
    ValveDlg
  },

  data: () => ({
    valves: [],
    pendingId: null,
    pendingDT: null,
    bgpr: null,
  }),

  created() {
    document.title = "Управление";
  },

  mounted() {
    this.fetchData();
    this.bgpr = setInterval(() => {
      this.fetchData()
    }, 1000)
  },

  beforeDestroy() {
    if (this.bgpr) {
      clearInterval(this.bgpr)
    }
  },

  methods: {
    // Обновление данных
    fetchData() {
      return this.$root.restGet('/valve/list')
          .then((res) => {
            this.valves = res
            this.checkPending()
          })
    },

    checkPending() {
      if (this.pendingId == null)
        return;
      for (let i = 0; i < this.valves.length; i++) {
        if (this.valves[i].ID == this.pendingId && this.valves[i].NEXT_STATE_DATE != this.pendingDT) {
          this.pendingId = null
          this.pendingDT = null
        }
      }
    },

    // Смена режима работы крана (автоматический/ручной)
    toggleControl(valve) {
      if (this.pendingId != null)
        return;
      this.pendingId = valve.ID
      this.$root.restPost('/valve/' + valve.ID + '/toggleControl')
          .then(() => {
            this.fetchData()
          })
          .finally(() => {
            this.pendingId = null
          })
    },

    // Смена состояния крана (открыт/закрыт)
    toggleState(valve) {
      if (this.pendingId != null || valve.AUTO_CONTROL === true)
        return;
      this.$root.restPost('/valve/' + valve.ID + '/toggleState')
          .then((res) => {
            this.pendingId = valve.ID
            this.pendingDT = res.next_state_date
            this.fetchData()
          })
    },

  },

};
</script>
