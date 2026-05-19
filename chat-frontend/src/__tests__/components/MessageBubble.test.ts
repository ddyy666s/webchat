import { describe, it, expect, vi } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import MessageBubble from '@/components/chat/MessageBubble.vue'

vi.mock('@/utils/date', () => ({
  formatRelativeTime: vi.fn(() => '2分钟前')
}))

vi.mock('@/api/message', () => ({
  recallMessageApi: vi.fn()
}))

describe('MessageBubble.vue', () => {
  const textMessage = {
    id: 1,
    fromUserId: 1,
    fromUserNickname: 'Alice',
    fromUserAvatar: '',
    messageType: 1,
    content: 'Hello World',
    sendTime: new Date().toISOString(),
    isRecalled: false
  }

  const imageMessage = {
    ...textMessage,
    id: 2,
    messageType: 2,
    content: 'https://example.com/image.jpg'
  }

  const voiceMessage = {
    ...textMessage,
    id: 3,
    messageType: 4,
    content: 'https://example.com/voice.mp3',
    duration: 30
  }

  it('renders text message content', () => {
    const wrapper = shallowMount(MessageBubble, {
      props: { message: textMessage, isOwn: false, showInfo: true }
    })
    expect(wrapper.text()).toContain('Hello World')
  })

  it('renders image message', () => {
    const wrapper = shallowMount(MessageBubble, {
      props: { message: imageMessage, isOwn: false }
    })
    expect(wrapper.findComponent({ name: 'ElImage' }).exists()).toBe(true)
  })

  it('renders voice message', () => {
    const wrapper = shallowMount(MessageBubble, {
      props: { message: voiceMessage, isOwn: false }
    })
    expect(wrapper.findComponent({ name: 'VoiceMessage' }).exists()).toBe(true)
  })

  it('renders recalled text when message is recalled', () => {
    const recalled = { ...textMessage, isRecalled: true }
    const wrapper = shallowMount(MessageBubble, {
      props: { message: recalled, isOwn: false }
    })
    expect(wrapper.text()).toContain('对方撤回了一条消息')
  })

  it('shows own recall text when own message is recalled', () => {
    const recalled = { ...textMessage, isRecalled: true }
    const wrapper = shallowMount(MessageBubble, {
      props: { message: recalled, isOwn: true }
    })
    expect(wrapper.text()).toContain('你撤回了一条消息')
  })

  it('applies own class for own messages', () => {
    const wrapper = shallowMount(MessageBubble, {
      props: { message: textMessage, isOwn: true }
    })
    expect(wrapper.find('.message-item.own').exists()).toBe(true)
  })

  it('shows sender info when showInfo is true', () => {
    const wrapper = shallowMount(MessageBubble, {
      props: { message: textMessage, isOwn: false, showInfo: true }
    })
    expect(wrapper.find('.message-info').exists()).toBe(true)
    expect(wrapper.find('.name').text()).toBe('Alice')
  })

  it('hides sender info when showInfo is false', () => {
    const wrapper = shallowMount(MessageBubble, {
      props: { message: textMessage, isOwn: false, showInfo: false }
    })
    expect(wrapper.find('.message-info').exists()).toBe(false)
  })
})
