import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import MessageInput from '@/components/chat/MessageInput.vue'

describe('MessageInput.vue', () => {
  it('renders textarea', () => {
    const wrapper = mount(MessageInput, { props: { currentChatUserId: 1 } })
    expect(wrapper.find('textarea').exists()).toBe(true)
  })

  it('renders CommunicationBar', () => {
    const wrapper = mount(MessageInput, { props: { currentChatUserId: 1 } })
    expect(wrapper.findComponent({ name: 'CommunicationBar' }).exists()).toBe(true)
  })

  it('passes currentChatUserId to CommunicationBar', () => {
    const wrapper = mount(MessageInput, { props: { currentChatUserId: 5 } })
    const bar = wrapper.findComponent({ name: 'CommunicationBar' })
    expect(bar.exists()).toBe(true)
    if (bar.exists()) expect(bar.props('currentChatUserId')).toBe(5)
  })

  it('does not emit send when content is empty', async () => {
    const wrapper = mount(MessageInput, { props: { currentChatUserId: 1 } })
    const btn = wrapper.find('button')
    if (btn.exists()) await btn.trigger('click')
    expect(wrapper.emitted('send')).toBeFalsy()
  })
})
