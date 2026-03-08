export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.vueApp.directive('phone', {
    mounted(el: HTMLInputElement) {
      // 1. input 타입과 maxlength 속성을 자동으로 11로 설정
      el.setAttribute('maxlength', '11')
      
      const handleInput = (e: Event) => {
        const target = e.target as HTMLInputElement
        const originalValue = target.value
        
        // 2. 숫자가 아닌 문자 제거
        let numericValue = originalValue.replace(/[^0-9]/g, '')
        
        // 3. 길이가 11자를 넘어가면 잘라내기 (maxlength와 함께 이중 방어)
        if (numericValue.length > 11) {
          numericValue = numericValue.substring(0, 11)
        }
        
        // 값이 변경된 경우에만 업데이트 및 Vue 이벤트 트리거
        if (originalValue !== numericValue) {
          target.value = numericValue
          // v-model 에 값 변경을 알리기 위함
          target.dispatchEvent(new Event('input'))
        }
      }
      
      // keypress 이벤트 시 숫자 이외의 키 입력 차단
      const handleKeypress = (e: KeyboardEvent) => {
        // 백스페이스, 탭 등의 제어 키는 허용
        if (e.key.length === 1 && !/^[0-9]$/.test(e.key)) {
          e.preventDefault()
        }
      }

      el.addEventListener('input', handleInput)
      el.addEventListener('keypress', handleKeypress)
      
      // Cleanup용 이벤트 저장
      ;(el as any)._cleanupPhoneDir = () => {
        el.removeEventListener('input', handleInput)
        el.removeEventListener('keypress', handleKeypress)
      }
    },
    unmounted(el: HTMLInputElement) {
      if ((el as any)._cleanupPhoneDir) {
        (el as any)._cleanupPhoneDir()
      }
    }
  })
})
