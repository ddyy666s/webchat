import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import ImpressionItem from '@/components/impression/ImpressionItem.vue'

vi.mock('@/utils/date', () => ({
  formatDate: vi.fn(() => '2024-01-15 14:30')
}))

describe('ImpressionItem.vue', () => {
  const impression = {
    id: 1,
    content: '很靠谱的同事',
    fromUserId: 1,
    fromUserNickname: 'Alice',
    fromUserAvatar: '',
    toUserId: 2,
    toUserNickname: 'Bob',
    toUserAvatar: '',
    createdAt: '2024-01-15T14:30:00Z'
  }

  it('renders impression content', () => {
    const wrapper = shallowMount(ImpressionItem, {
      props: { impression, type: 'to-me' }
    })
    expect(wrapper.find('.text').text()).toBe('很靠谱的同事')
  })

  it('renders from user name for to-me type', () => {
    const wrapper = shallowMount(ImpressionItem, {
      props: { impression, type: 'to-me' }
    })
    expect(wrapper.find('.name').text()).toBe('Alice')
  })

  it('renders to user name for by-me type', () => {
    const wrapper = shallowMount(ImpressionItem, {
      props: { impression, type: 'by-me' }
    })
    expect(wrapper.find('.name').text()).toBe('Bob')
  })

  it('renders timestamp', () => {
    const wrapper = shallowMount(ImpressionItem, {
      props: { impression, type: 'to-me' }
    })
    expect(wrapper.find('.time').exists()).toBe(true)
  })

  it('shows delete button for by-me type', () => {
    const wrapper = shallowMount(ImpressionItem, {
      props: { impression, type: 'by-me' }
    })
    expect(wrapper.find('.delete-btn').exists()).toBe(true)
  })

  it('does not show delete button for to-me type', () => {
    const wrapper = shallowMount(ImpressionItem, {
      props: { impression, type: 'to-me' }
    })
    expect(wrapper.find('.delete-btn').exists()).toBe(false)
  })

  it('emits delete event when delete button is clicked', async () => {
    const wrapper = shallowMount(ImpressionItem, {
      props: { impression, type: 'by-me' }
    })
    await wrapper.find('.delete-btn').trigger('click')
    expect(wrapper.emitted('delete')).toBeTruthy()
    expect(wrapper.emitted('delete')![0]).toEqual([1])
  })
})
