import Noty from 'noty'

const defaults = {
  layout: 'bottomRight',
  theme: 'mint',
  timeout: 5000,
  progressBar: true,
  closeWith: ['click', 'button'],
  animation: {
    open: null,
  }
}

const VueNoty = {
  options: {},

  setOptions (opts) {
    this.options = Object.assign({}, defaults, opts)
    return this
  },

  create (params) {
    return new Noty(params)
  },

  show (text, type = 'alert', opts = {}) {
    const params = Object.assign({}, this.options, opts, {
      type,
      text
    })

    const noty = this.create(params)
    noty.show()
    return noty
  },

  // Добавление иконки в текст
  makeText(title, text, icon) {
    return '<table><tr>' +
      (icon == null ? '' : '<td><i class="noty-icon fa fa-' + icon + '"/></td>') +
      '<td>' +
      (title == null ? '' : '<p class="noty-title">' + title + '</p>') +
      (text == null ? '' : '<p class="noty-text">' + text + '</p>') +
      '</td></tr></table>'
  },

  success (title, text, opts = {}) {
    return this.show(this.makeText(title, text, 'check-circle'), 'success', opts)
  },

  error (title, text, opts = {}) {
    opts.closeWith = ['button']
    return this.show(this.makeText(title, text, 'exclamation-triangle'), 'error', opts)
  },

  warning (title, text, opts = {}) {
    opts.closeWith = ['button']
    return this.show(this.makeText(title, text, 'exclamation-circle'), 'warning', opts)
  },

  info (title, text, opts = {}) {
    return this.show(this.makeText(title, text, 'info-circle'), 'info', opts)
  }
}

export default {
  install: function (Vue, opts) {
    const noty = VueNoty.setOptions(opts)
    Vue.prototype.$noty = noty
    Vue.noty = noty
  }
}
