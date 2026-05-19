import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import InfiniteScroll from '@/components/common/InfiniteScroll.vue'

describe('InfiniteScroll.vue', () => {
  it('renders slot content', () => {
    const wrapper = mount(InfiniteScroll, { props: { loading: false, hasMore: true }, slots: { default: '<div class="content">Items</div>' } })
    expect(wrapper.find('.content').exists()).toBe(true)
  })

  it('shows loading indicator when loading', () => {
    const wrapper = mount(InfiniteScroll, { props: { loading: true, hasMore: true } })
    expect(wrapper.text()).toContain('加载')
  })

  it('does not show loading indicator when not loading', () => {
    const wrapper = mount(InfiniteScroll, { props: { loading: false, hasMore: true } })
    expect(wrapper.text()).not.toContain('加载')
  })

  it('shows error message when error prop is true', () => {
    const wrapper = mount(InfiniteScroll, { props: { loading: false, hasMore: true, error: true, errorMessage: '加载失败，请重试' } })
    expect(wrapper.text()).toContain('加载失败')
  })

  it('does not emit load when loading is true', () => {
    const wrapper = mount(InfiniteScroll, { props: { loading: true, hasMore: true } })
    const container = wrapper.find('.infinite-scroll-container')
    if (container.exists()) container.trigger('scroll')
    expect(wrapper.emitted('load')).toBeFalsy()
  })
})
