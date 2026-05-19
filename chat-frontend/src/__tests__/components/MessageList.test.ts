import { describe, it, expect, vi } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import MessageList from '@/components/chat/MessageList.vue'

vi.mock('@/utils/date', () => ({
  formatRelativeTime: vi.fn(() => '2分钟前')
}))

describe('MessageList.vue', () => {
  const messages = [
    { id: 1, fromUserId: 1, fromUserNickname: 'Alice', fromUserAvatar: '', messageType: 1, content: 'Hi', sendTime: new Date().toISOString(), isRecalled: false },
    { id: 2, fromUserId: 2, fromUserNickname: 'Bob', fromUserAvatar: '', messageType: 1, content: 'Hello', sendTime: new Date().toISOString(), isRecalled: false }
  ]

  it('renders list of messages', () => {
    const wrapper = shallowMount(MessageList, {
      props: { messages, currentUserId: 1, loading: false }
    })
    const bubbles = wrapper.findAllComponents({ name: 'MessageBubble' })
    expect(bubbles.length).toBe(2)
  })

  it('shows loading skeleton when loading and no messages', () => {
    const wrapper = shallowMount(MessageList, {
      props: { messages: [], currentUserId: 1, loading: true }
    })
    expect(wrapper.find('.loading').exists()).toBe(true)
  })

  it('shows loading more indicator when loading with existing messages', () => {
    const wrapper = shallowMount(MessageList, {
      props: { messages, currentUserId: 1, loading: true }
    })
    expect(wrapper.find('.loading-more').exists()).toBe(true)
  })

  it('does not show loading-more when not loading', () => {
    const wrapper = shallowMount(MessageList, {
      props: { messages, currentUserId: 1, loading: false }
    })
    expect(wrapper.find('.loading-more').exists()).toBe(false)
  })

  it('passes isOwn correctly based on currentUserId', () => {
    const wrapper = shallowMount(MessageList, {
      props: { messages, currentUserId: 1, loading: false }
    })
    const bubbles = wrapper.findAllComponents({ name: 'MessageBubble' })
    expect(bubbles[0].props('isOwn')).toBe(true)
    expect(bubbles[1].props('isOwn')).toBe(false)
  })

  it('emits loadMore when scrolled to bottom', async () => {
    const wrapper = shallowMount(MessageList, {
      props: { messages, currentUserId: 1, loading: false }
    })
    const listEl = wrapper.find('.message-list')
    Object.defineProperty(listEl.element, 'scrollHeight', { value: 1000 })
    Object.defineProperty(listEl.element, 'clientHeight', { value: 500 })
    Object.defineProperty(listEl.element, 'scrollTop', { value: 500 })
    await listEl.trigger('scroll')
    expect(wrapper.emitted('loadMore')).toBeTruthy()
  })

  it('has scrollBottomRef element', () => {
    const wrapper = shallowMount(MessageList, {
      props: { messages, currentUserId: 1, loading: false }
    })
    const el = wrapper.find('.message-list')
    expect(el.exists()).toBe(true)
  })
})
