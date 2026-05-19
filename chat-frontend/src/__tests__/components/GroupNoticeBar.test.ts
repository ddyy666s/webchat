import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import GroupNoticeBar from '@/components/group/GroupNoticeBar.vue'

describe('GroupNoticeBar.vue', () => {
  it('renders notice text', () => {
    const wrapper = shallowMount(GroupNoticeBar, {
      props: { notice: '今晚有线上会议，请准时参加' }
    })
    expect(wrapper.find('.notice-text').text()).toBe('今晚有线上会议，请准时参加')
  })

  it('does not render when notice is null', () => {
    const wrapper = shallowMount(GroupNoticeBar, {
      props: { notice: null }
    })
    expect(wrapper.find('.notice-bar').exists()).toBe(false)
  })

  it('does not render when notice is empty string', () => {
    const wrapper = shallowMount(GroupNoticeBar, {
      props: { notice: '' }
    })
    expect(wrapper.find('.notice-bar').exists()).toBe(false)
  })

  it('emits click event when clicked', async () => {
    const wrapper = shallowMount(GroupNoticeBar, {
      props: { notice: 'Test notice' }
    })
    await wrapper.find('.notice-bar').trigger('click')
    expect(wrapper.emitted('click')).toBeTruthy()
  })

  it('shows detail button', () => {
    const wrapper = shallowMount(GroupNoticeBar, {
      props: { notice: 'Test notice' }
    })
    expect(wrapper.find('.notice-bar').text()).toContain('Test notice')
  })
})
