import { describe, it, expect, vi } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import FriendItem from '@/components/friend/FriendItem.vue'

describe('FriendItem.vue', () => {
  const baseFriend = {
    userId: 1,
    nickname: 'Alice',
    avatar: '',
    isOnline: true,
    remark: 'Ali',
    signature: 'Hey there!',
    unreadCount: 0
  }

  it('renders friend nickname', () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: baseFriend, isActive: false }
    })
    expect(wrapper.find('.name').text()).toBe('Ali')
  })

  it('renders nickname when no remark', () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: { ...baseFriend, remark: '' }, isActive: false }
    })
    expect(wrapper.find('.name').text()).toBe('Alice')
  })

  it('shows online dot when friend is online', () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: { ...baseFriend, isOnline: true }, isActive: false }
    })
    expect(wrapper.find('.online-dot.online').exists()).toBe(true)
  })

  it('shows offline dot when friend is offline', () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: { ...baseFriend, isOnline: false }, isActive: false }
    })
    expect(wrapper.find('.online-dot.online').exists()).toBe(false)
  })

  it('shows unread badge when friend has unread messages', () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: { ...baseFriend, unreadCount: 5 }, isActive: false }
    })
    expect(wrapper.find('.unread-badge').exists()).toBe(true)
    expect(wrapper.find('.unread-badge').text()).toBe('5')
  })

  it('shows 99+ when unread count exceeds 99', () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: { ...baseFriend, unreadCount: 100 }, isActive: false }
    })
    expect(wrapper.find('.unread-badge').text()).toBe('99+')
  })

  it('does not show unread badge when count is 0', () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: { ...baseFriend, unreadCount: 0 }, isActive: false }
    })
    expect(wrapper.find('.unread-badge').exists()).toBe(false)
  })

  it('applies active class when isActive is true', () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: baseFriend, isActive: true }
    })
    expect(wrapper.find('.friend-item.active').exists()).toBe(true)
  })

  it('emits click event when clicked', async () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: baseFriend, isActive: false }
    })
    await wrapper.find('.friend-item').trigger('click')
    expect(wrapper.emitted('click')).toBeTruthy()
  })

  it('renders signature as message', () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: baseFriend, isActive: false }
    })
    expect(wrapper.find('.message').text()).toBe('Hey there!')
  })

  it('renders default message when no signature', () => {
    const wrapper = shallowMount(FriendItem, {
      props: { friend: { ...baseFriend, signature: '' }, isActive: false }
    })
    expect(wrapper.find('.message').text()).toBe('这个人很懒，什么都没写')
  })
})
