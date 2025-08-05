// 아이별 색상 관리 유틸리티

// 사용 가능한 색상 팔레트 (구별하기 쉬운 색상들)
const COLOR_PALETTE = [
  '#8B5CF6', // 보라색
  '#3B82F6', // 파란색
  '#EF4444', // 빨간색
  '#10B981', // 녹색
  '#F59E0B', // 주황색
  '#EC4899', // 핑크색
  '#8B5A2B', // 갈색
  '#6366F1', // 인디고
  '#84CC16', // 라임
  '#F97316', // 오렌지
  '#06B6D4', // 사이안
  '#A855F7', // 바이올렛
  '#DC2626', // 진한 빨강
  '#059669', // 에메랄드
  '#D97706'  // 앰버
]

// 이미 사용된 색상들을 가져오기
function getUsedColors() {
  const children = JSON.parse(localStorage.getItem('children') || '[]')
  return children.map(child => child.color).filter(Boolean)
}

// 새로운 색상 할당
export function assignColorToChild(childId) {
  const usedColors = getUsedColors()
  
  // 사용되지 않은 첫 번째 색상 찾기
  const availableColor = COLOR_PALETTE.find(color => !usedColors.includes(color))
  
  // 모든 색상이 사용된 경우 랜덤으로 선택
  const assignedColor = availableColor || COLOR_PALETTE[Math.floor(Math.random() * COLOR_PALETTE.length)]
  
  return assignedColor
}

// 아이의 색상 가져오기
export function getChildColor(childName) {
  const children = JSON.parse(localStorage.getItem('children') || '[]')
  const child = children.find(c => c.name === childName)
  
  if (child && child.color) {
    return child.color
  }
  
  // 기존 데이터에서 색상 찾기 (하위 호환성)
  const legacyColors = {
    '김미래': '#8B5CF6',
    '김과거': '#3B82F6'
  }
  
  return legacyColors[childName] || '#8B5CF6'
}

// 모든 아이의 색상 정보 가져오기
export function getAllChildColors() {
  const children = JSON.parse(localStorage.getItem('children') || '[]')
  const colors = {}
  
  children.forEach(child => {
    colors[child.name] = child.color || getChildColor(child.name)
  })
  
  return colors
}

// 아이 색상 업데이트 (필요한 경우)
export function updateChildColor(childId, newColor) {
  const children = JSON.parse(localStorage.getItem('children') || '[]')
  const childIndex = children.findIndex(c => c.id === childId)
  
  if (childIndex !== -1) {
    children[childIndex].color = newColor
    localStorage.setItem('children', JSON.stringify(children))
    return true
  }
  
  return false
}

// 기존 아이들에게 색상이 없는 경우 할당
export function ensureAllChildrenHaveColors() {
  const children = JSON.parse(localStorage.getItem('children') || '[]')
  let updated = false
  
  children.forEach((child, index) => {
    if (!child.color) {
      const usedColors = children.slice(0, index).map(c => c.color).filter(Boolean)
      const availableColor = COLOR_PALETTE.find(color => !usedColors.includes(color))
      child.color = availableColor || COLOR_PALETTE[index % COLOR_PALETTE.length]
      updated = true
      console.log(`색상 할당됨: ${child.name} -> ${child.color}`)
    }
  })
  
  if (updated) {
    localStorage.setItem('children', JSON.stringify(children))
    console.log('아이들 색상 정보 업데이트됨:', children.map(c => `${c.name}: ${c.color}`))
  }
  
  return children
}

// 디버깅용 - 현재 색상 현황 출력
export function debugColors() {
  const children = JSON.parse(localStorage.getItem('children') || '[]')
  console.log('=== 현재 아이들 색상 현황 ===')
  children.forEach(child => {
    console.log(`${child.name}: ${child.color || '색상 없음'}`)
  })
  console.log('사용된 색상:', getUsedColors())
  console.log('사용 가능한 색상:', COLOR_PALETTE.filter(color => !getUsedColors().includes(color)))
}