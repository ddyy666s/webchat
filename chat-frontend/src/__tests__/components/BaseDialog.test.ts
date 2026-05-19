import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import BaseDialog from '@/components/common/BaseDialog.vue'

describe('BaseDialog.vue', () => {
  const createWrapper = (props = {}, slots = {}) =>
    shallowMount(BaseDialog, { props: { modelValue: true, ...props }, slots, global: { stubs: { 'el-dialog': { template: '<div class="el-dialog-stub"><slot name="header" /><slot /><slot name="footer" /></div>' } } } })

  it('renders with title', () => {
    const wrapper = createWrapper({ title: 'test' })
    expect(wrapper.exists()).toBe(true)
  })

  it('shows dialog when modelValue is true', () => {
    const wrapper = createWrapper()
    expect(wrapper.find('.el-dialog-stub').exists()).toBe(true)
  })

  it('renders default slot content', () => {
    const wrapper = createWrapper({}, { default: '<div class="content">X</div>' })
    expect(wrapper.find('.content').exists()).toBe(true)
  })

  it('renders header slot', () => {
    const wrapper = createWrapper({}, { header: 'Header' })
    expect(wrapper.text()).toContain('Header')
  })

  it('renders footer slot', () => {
    const wrapper = createWrapper({}, { footer: 'Footer' })
    expect(wrapper.text()).toContain('Footer')
  })
})
