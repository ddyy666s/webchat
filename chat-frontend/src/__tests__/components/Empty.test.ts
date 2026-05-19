import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import Empty from '@/components/common/Empty.vue'

describe('Empty.vue', () => {
  it('renders default title', () => {
    const wrapper = shallowMount(Empty)
    expect(wrapper.text()).toContain('暂无数据')
  })

  it('renders custom title', () => {
    const wrapper = shallowMount(Empty, { props: { title: 'custom title' } })
    expect(wrapper.text()).toContain('custom title')
  })

  it('renders description when provided', () => {
    const wrapper = shallowMount(Empty, { props: { description: 'no results found' } })
    expect(wrapper.text()).toContain('no results found')
  })

  it('does not render description when not provided', () => {
    const wrapper = shallowMount(Empty)
    expect(wrapper.find('.empty-description').exists()).toBe(false)
  })
})
