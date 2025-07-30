// tailwind.config.js
export default {
  content: ['./index.html', './src/**/*.{vue,js}'],
  theme: {
    extend: {
      colors: {
        background: {
          footer: '#F2F5FD',
          header: '#DBEAFE',
        },
        yellow: '#FFF7BB',
        purpleLight: '#F3E8FF',
        box: '#FFFFFF',
        greenLight: '#DCFCE7',
        hover: {
          blue: '#0918BE',
          purple: '#4F378A',
          red: '#FF0000',
        },
        transition: {
          blue: '#60A5FA',
          green: '#4ADE80',
        },
      },
      fontFamily: {
        paper: ['PaperlogyRegular', 'sans-serif'],
        paperSemi: ['PaperlogySemiBold', 'sans-serif'],
        paperBold: ['PaperlogyBold', 'sans-serif'],
        shark: ['BabyShark', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
