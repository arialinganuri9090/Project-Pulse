import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'

export default createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        colors: {
          primary: '#1565C0',
          secondary: '#0D47A1',
          accent: '#42A5F5',
          error: '#D32F2F',
          warning: '#F57F17',
          info: '#0288D1',
          success: '#2E7D32',
        }
      }
    }
  }
})
