import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import FriendGroup from '@/components/friend/FriendGroup.vue'

describe('FriendGroup.vue', () => {
  const friends = [
    { userId: 1, nickname: 'Alice', avatar: '', isOnline: true, remark: '', signature: '' },
    { userId: 2, nickname: 'Bob', avatar: '', isOnline: false, remark: '', signature: '' }
  ]

  it('renders group name', () => {
    const wrapper = shallowMount(FriendGroup, {
      props: { groupName: '我的好友', friends, currentChatUserId: null }
    })
    expect(wrapper.find('.group-header').text()).toContain('我的好友')
  })

  it('renders friend count', () => {
    const wrapper = shallowMount(FriendGroup, {
      props: { groupName: '我的好友', friends, currentChatUserId: null }
    })
    expect(wrapper.find('.count').text()).toBe('2')
  })

  it('renders FriendItem for each friend', () => {
    const wrapper = shallowMount(FriendGroup, {
      props: { groupName: '我的好友', friends, currentChatUserId: null }
    })
    const items = wrapper.findAllComponents({ name: 'FriendItem' })
    expect(items.length).toBe(2)
  })

  it('passes isActive correctly', () => {
    const wrapper = shallowMount(FriendGroup, {
      props: { groupName: '我的好友', friends, currentChatUserId: 1 }
    })
    const items = wrapper.findAllComponents({ name: 'FriendItem' })
    expect(items[0].props('isActive')).toBe(true)
    expect(items[1].props('isActive')).toBe(false)
  })

  it('emits selectChat when FriendItem emits click', () => {
    const wrapper = shallowMount(FriendGroup, {
      props: { groupName: '我的好友', friends, currentChatUserId: null }
    })
    const item = wrapper.findComponent({ name: 'FriendItem' })
    item.vm.$emit('click')
    expect(wrapper.emitted('selectChat')).toBeTruthy()
  })

  it('emits command when FriendItem emits command', () => {
    const wrapper = shallowMount(FriendGroup, {
      props: { groupName: '我的好友', friends, currentChatUserId: null }
    })
    const item = wrapper.findComponent({ name: 'FriendItem' })
    item.vm.$emit('command', 'delete', friends[0])
    expect(wrapper.emitted('command')).toBeTruthy()
    expect(wrapper.emitted('command')![0]).toEqual(['delete', friends[0]])
  })
})
