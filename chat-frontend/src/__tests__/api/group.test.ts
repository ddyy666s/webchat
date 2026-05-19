import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '@/api/request'
import {
  createGroupApi, getGroupListApi, getGroupDetailApi, getGroupHistoryApi,
  getGroupMembersApi, inviteMemberApi, quitGroupApi, disbandGroupApi,
  clearGroupUnreadApi, updateGroupNoticeApi
} from '@/api/group'

vi.mock('@/api/request', () => ({
  default: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() }
}))

describe('groupApi', () => {
  beforeEach(() => { vi.clearAllMocks() })

  it('createGroupApi sends POST /group', async () => {
    const data = { name: 'group1', memberIds: [1, 2] }
    await createGroupApi(data)
    expect(request.post).toHaveBeenCalledWith('/group', data)
  })

  it('getGroupListApi sends GET /group/list', async () => {
    await getGroupListApi()
    expect(request.get).toHaveBeenCalledWith('/group/list')
  })

  it('getGroupDetailApi sends GET /group/:id', async () => {
    await getGroupDetailApi(10)
    expect(request.get).toHaveBeenCalledWith('/group/10')
  })

  it('getGroupHistoryApi sends GET /group/message/:id with pagination', async () => {
    await getGroupHistoryApi(1, 2, 50)
    expect(request.get).toHaveBeenCalledWith('/group/message/1', { params: { page: 2, size: 50 } })
  })

  it('getGroupHistoryApi defaults to page 1 size 20', async () => {
    await getGroupHistoryApi(1)
    expect(request.get).toHaveBeenCalledWith('/group/message/1', { params: { page: 1, size: 20 } })
  })

  it('getGroupMembersApi sends GET /group/:id/members', async () => {
    await getGroupMembersApi(5)
    expect(request.get).toHaveBeenCalledWith('/group/5/members')
  })

  it('inviteMemberApi sends POST /group/invite', async () => {
    await inviteMemberApi({ groupId: 1, userId: 2 })
    expect(request.post).toHaveBeenCalledWith('/group/invite', { groupId: 1, userId: 2 })
  })

  it('quitGroupApi sends DELETE /group/:id/quit', async () => {
    await quitGroupApi(3)
    expect(request.delete).toHaveBeenCalledWith('/group/3/quit')
  })

  it('disbandGroupApi sends DELETE /group/:id/disband', async () => {
    await disbandGroupApi(7)
    expect(request.delete).toHaveBeenCalledWith('/group/7/disband')
  })

  it('clearGroupUnreadApi sends PUT /group/:id/read', async () => {
    await clearGroupUnreadApi(2)
    expect(request.put).toHaveBeenCalledWith('/group/2/read')
  })

  it('updateGroupNoticeApi sends PUT /group/:id/notice', async () => {
    await updateGroupNoticeApi(1, 'new notice')
    expect(request.put).toHaveBeenCalledWith('/group/1/notice', { notice: 'new notice' })
  })
})
