module.exports = {
  "transpileDependencies": [
    "vuetify"
  ],

  devServer: {
    proxy: {
      '/': {
        target: 'http://localhost:8881',
        ws: true,
        changeOrigin: true,
        autoRewrite: true
      }
    }
  }

}