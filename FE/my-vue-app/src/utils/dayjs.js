import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'

dayjs.extend(utc)
dayjs.extend(timezone)

// 한국 시간대 설정
dayjs.tz.setDefault('Asia/Seoul')

export default dayjs