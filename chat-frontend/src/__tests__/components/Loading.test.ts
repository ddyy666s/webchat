import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import Loading from '@/components/common/Loading.vue'

describe('Loading.vue', () => {
  it('renders when visible is true', () => {
    const wrapper = shallowMount(Loading, { props: { visible: true } })
    expect(wrapper.find('.loading-container').exists()).toBe(true)
  })

  it('does not render when visible is false', () => {
    const wrapper = shallowMount(Loading, { props: { visible: false } })
    expect(wrapper.find('.loading-container').exists()).toBe(false)
  })

  it('renders default loading text', () => {
    const wrapper = shallowMount(Loading, { props: { visible: true } })
    expect(wrapper.text()).toContain('加载中...')
  })

  it('renders custom loading text', () => {
    const wrapper = shallowMount(Loading, { props: { visible: true, text: 'Please wait' } })
    expect(wrapper.text()).toContain('Please wait')
  })
})
