import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import FriendList from '@/components/friend/FriendList.vue'

vi.mock('@/stores/friendStore', () => ({ useFriendStore: () => ({ friendList: [], friendRequests: [], loadFriendList: vi.fn(), loadFriendRequests: vi.fn() }) }))
vi.mock('vue-router', () => ({ useRoute: vi.fn(() => ({ query: {} })), useRouter: vi.fn(() => ({ push: vi.fn() })) }))
vi.mock('element-plus', () => ({ ElMessage: { success: vi.fn(), error: vi.fn() } }))

describe('FriendList.vue', () => {
  it('renders without errors', () => {
    const wrapper = mount(FriendList, { props: { currentChatUserId: null }, global: { stubs: { FriendGroup: true, AddFriendDialog: true, ConfirmDialog: true, PromptDialog: true, 'el-scrollbar': true } } })
    expect(wrapper.exists()).toBe(true)
  })
})
