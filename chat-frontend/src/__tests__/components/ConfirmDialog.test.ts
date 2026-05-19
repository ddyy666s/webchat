import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const stubs = {
  'el-dialog': {
    template: '<div class="el-dialog-stub"><slot /><slot name="footer" /></div>',
    props: ['modelValue']
  },
  'el-button': {
    template: '<button :class="$attrs.class" @click="$emit(\'click\')"><slot /></button>'
  },
  'el-icon': { template: '<i><slot /></i>' }
}

describe('ConfirmDialog.vue', () => {
  it('renders message content', () => {
    const wrapper = shallowMount(ConfirmDialog, {
      props: { modelValue: true, title: 'Confirm', message: 'Are you sure?' },
      global: { stubs }
    })
    expect(wrapper.text()).toContain('Are you sure?')
  })

  it('emits confirm when confirm button is clicked', async () => {
    const wrapper = shallowMount(ConfirmDialog, {
      props: { modelValue: true, message: 'test', confirmText: '确定', cancelText: '取消' },
      global: { stubs }
    })
    await wrapper.find('.btn-confirm').trigger('click')
    expect(wrapper.emitted('confirm')).toBeTruthy()
  })

  it('emits cancel when cancel button is clicked', async () => {
    const wrapper = shallowMount(ConfirmDialog, {
      props: { modelValue: true, message: 'test', confirmText: '确定', cancelText: '取消' },
      global: { stubs }
    })
    await wrapper.find('.btn-cancel').trigger('click')
    expect(wrapper.emitted('cancel')).toBeTruthy()
  })

  it('closes dialog after confirm', async () => {
    const wrapper = shallowMount(ConfirmDialog, {
      props: { modelValue: true, message: 'test', confirmText: '确定', cancelText: '取消' },
      global: { stubs }
    })
    await wrapper.find('.btn-confirm').trigger('click')
    expect(wrapper.emitted('update:modelValue')?.[0]).toEqual([false])
  })

  it('renders confirm-icon element', () => {
    const wrapper = shallowMount(ConfirmDialog, {
      props: { modelValue: true, message: 'test' },
      global: { stubs }
    })
    expect(wrapper.find('.confirm-icon').exists()).toBe(true)
  })
})
