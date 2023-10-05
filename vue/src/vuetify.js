import Vue from 'vue';
import Vuetify from 'vuetify/lib';
import '@mdi/font/css/materialdesignicons.css'
import theme from '@/theme'

Vue.use(Vuetify);

export default new Vuetify({
  icons: {
    iconfont: 'mdi'
  },

  theme: {
    themes: theme,

    options: {
      customProperties: true,
    },

    minifyTheme: function (css) {
      return process.env.NODE_ENV === 'production'
        ? css.replace(/[\r\n|\r|\n]/g, '')
        : css
    },

  }
});
