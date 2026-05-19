import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import GroupList from '@/views/layout/sidebar/GroupList.vue'

describe('GroupList.vue', () => {
  const groups = [
    { id: 1, name: '前端交流群', avatar: '', memberCount: 42, createdAt: new Date().toISOString(), unreadCount: 3 },
    { id: 2, name: '项目组', avatar: '', memberCount: 12, createdAt: new Date().toISOString(), unreadCount: 0 }
  ]

  it('renders group list', () => {
    const wrapper = shallowMount(GroupList, { props: { groups, currentGroupId: null } })
    expect(wrapper.findAll('.group-item').length).toBe(2)
  })

  it('renders group names', () => {
    const wrapper = shallowMount(GroupList, { props: { groups, currentGroupId: null } })
    expect(wrapper.text()).toContain('前端交流群')
    expect(wrapper.text()).toContain('项目组')
  })

  it('shows member count', () => {
    const wrapper = shallowMount(GroupList, { props: { groups, currentGroupId: null } })
    expect(wrapper.text()).toContain('42人')
  })

  it('highlights active group', () => {
    const wrapper = shallowMount(GroupList, { props: { groups, currentGroupId: 1 } })
    expect(wrapper.findAll('.group-item')[0].classes()).toContain('active')
    expect(wrapper.findAll('.group-item')[1].classes()).not.toContain('active')
  })

  it('shows unread badge', () => {
    const wrapper = shallowMount(GroupList, { props: { groups, currentGroupId: null } })
    expect(wrapper.findAll('.unread-dot').length).toBe(1)
    expect(wrapper.find('.unread-dot').text()).toBe('3')
  })

  it('shows 99+ when unread exceeds 99', () => {
    const wrapper = shallowMount(GroupList, { props: { groups: [{ ...groups[0], unreadCount: 100 }], currentGroupId: null } })
    expect(wrapper.find('.unread-dot').text()).toBe('99+')
  })

  it('shows empty state when no groups', () => {
    const wrapper = shallowMount(GroupList, { props: { groups: [], currentGroupId: null } })
    expect(wrapper.find('.group-header-actions').exists()).toBe(true)
  })

  it('emits select when group item is clicked', async () => {
    const wrapper = shallowMount(GroupList, { props: { groups, currentGroupId: null } })
    await wrapper.findAll('.group-item')[0].trigger('click')
    expect(wrapper.emitted('select')).toBeTruthy()
    expect(wrapper.emitted('select')![0]).toEqual([groups[0]])
  })


})
