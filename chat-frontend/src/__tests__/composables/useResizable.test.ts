import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { defineComponent } from 'vue'
import { mount } from '@vue/test-utils'

import { useResizable } from '@/composables/useResizable'

const createTestComponent = (options?: Parameters<typeof useResizable>[0]) => defineComponent({
  setup() { return useResizable(options) },
  template: '<div/>'
})

describe('useResizable', () => {
  beforeEach(() => { localStorage.clear() })
  afterEach(() => { vi.restoreAllMocks() })

  it('starts with default width', () => {
    const wrapper = mount(createTestComponent())
    expect(wrapper.vm.sidebarWidth).toBe(420)
  })

  it('starts not resizing', () => {
    const wrapper = mount(createTestComponent())
    expect(wrapper.vm.isResizing).toBe(false)
  })

  it('accepts custom options', () => {
    const wrapper = mount(createTestComponent({ defaultWidth: 300 }))
    expect(wrapper.vm.sidebarWidth).toBe(300)
  })

  it('loadWidth restores saved width from localStorage', () => {
    localStorage.setItem('sidebar-width', '500')
    const wrapper = mount(createTestComponent())
    expect(wrapper.vm.sidebarWidth).toBe(500)
  })

  it('loadWidth ignores saved width outside min/max range', () => {
    localStorage.setItem('sidebar-width', '9999')
    const wrapper = mount(createTestComponent())
    expect(wrapper.vm.sidebarWidth).toBe(420)
  })

  it('startResize sets isResizing and handles mousemove', () => {
    const wrapper = mount(createTestComponent())
    const addSpy = vi.spyOn(document, 'addEventListener')
    const mouseEvent = { preventDefault: vi.fn(), clientX: 100 } as unknown as MouseEvent
    wrapper.vm.startResize(mouseEvent)
    expect(mouseEvent.preventDefault).toHaveBeenCalled()
    expect(wrapper.vm.isResizing).toBe(true)
    expect(document.body.style.cursor).toBe('col-resize')
    const moveHandler = addSpy.mock.calls.find(c => c[0] === 'mousemove')![1] as (e: MouseEvent) => void
    moveHandler({ clientX: 200 } as MouseEvent)
    expect(wrapper.vm.sidebarWidth).toBe(520)
  })

  it('startResize handles mouseup to save and stop', () => {
    const setItemSpy = vi.spyOn(Storage.prototype, 'setItem')
    const removeSpy = vi.spyOn(document, 'removeEventListener')
    const wrapper = mount(createTestComponent())
    const addSpy = vi.spyOn(document, 'addEventListener')
    wrapper.vm.startResize({ preventDefault: vi.fn(), clientX: 100 } as unknown as MouseEvent)
    const upHandler = addSpy.mock.calls.find(c => c[0] === 'mouseup')![1] as () => void
    upHandler()
    expect(wrapper.vm.isResizing).toBe(false)
    expect(document.body.style.cursor).toBe('')
    expect(setItemSpy).toHaveBeenCalledWith('sidebar-width', String(wrapper.vm.sidebarWidth))
    expect(removeSpy).toHaveBeenCalledWith('mousemove', expect.any(Function))
    expect(removeSpy).toHaveBeenCalledWith('mouseup', expect.any(Function))
  })
})
